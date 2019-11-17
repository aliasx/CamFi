package nz.datawave.camfi.filters

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.squareup.picasso.Picasso
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_filters.*
import kotlinx.android.synthetic.main.item_gallery.imageView
import nz.datawave.camfi.*
import nz.datawave.camfi.extensions.*
import java.io.File
import com.squareup.picasso.Callback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.lang.Exception


class FiltersActivity : AppCompatActivity() {
    private lateinit var context: FiltersActivity
    private var click: Subject<Int> = PublishSubject.create()
    private var sourceBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        initUI()
        initPreview()
    }

    private fun initUI(){
        supportActionBar!!.title = "Filters"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        progressBar.setOnClickListener{}
    }

    private fun initPreview() {
        this.intent.extras?.let {
            val path = it.get(INTENT_IMAGE_PATH) as String
            click.subscribe(this::onSelectFilter)
            context = this
            Picasso.get()
                .load(File(path))
                .into(imageView, object : Callback {
                    override fun onError(e: Exception?) {
                    }

                    override fun onSuccess() {
                        sourceBitmap = imageView.drawable.toBitmap()
                        sourceBitmap?.let { bmp ->
                            imageView.setImageBitmap(bmp)
                            val images = createFilteredThumbs(bmp)
                            filtersGallery.layoutManager =
                                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                            filtersGallery.adapter = FilterAdapter(context, images, click)
                        }
                    }
                })
        }
    }

    private fun onSelectFilter(filterIndex: Int) {
        sourceBitmap?.let {
            when (filterIndex) {
                FILTER_NO -> imageView.setImageBitmap(it)
                FILTER_COLOR_RED -> imageView.setImageBitmap(it.changeColor())
                FILTER_COLOR_INVERSE -> imageView.setImageBitmap(it.invertColor())
                FILTER_BALLOONS -> imageView.setImageBitmap(it.balloons(this))
                FILTER_FRAME -> imageView.setImageBitmap(it.frame(this))
            }
        }
    }

    private fun createFilteredThumbs(bitmap: Bitmap): ArrayList<Bitmap> {
        val res: ArrayList<Bitmap> = ArrayList()
        val resizedBitmap = bitmap.resize(THUMB_SIZE)
        res.add(resizedBitmap)
        res.add(resizedBitmap.changeColor())
        res.add(resizedBitmap.invertColor())
        res.add(resizedBitmap.balloons(this))
        res.add(resizedBitmap.frame(this))

        return res
    }


    private fun share(){
        CoroutineScope(Dispatchers.Main).launch {
            progressBar.visibility = View.VISIBLE
            val intent: Intent? = async(Dispatchers.IO) {
                val bitmap = imageView.drawable.toBitmap()
                val file =
                    File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share.png")
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
                val uri =
                    FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)

                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.type = "image/jpeg"

                return@async intent
            }.await()

            intent?.let { context.startActivity(Intent.createChooser(it, "Share")) }
            progressBar.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_filter, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_share -> {
                share()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
