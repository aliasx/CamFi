package nz.datawave.camfi.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_gallery.view.*
import com.squareup.picasso.Picasso
import io.reactivex.subjects.Subject
import nz.datawave.camfi.R
import nz.datawave.camfi.THUMB_SIZE
import java.io.File

class GalleryAdapter(
    private val c: Context,
    private val images: ArrayList<String>,
    private val click: Subject<String>
) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    override fun getItemCount():Int = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(c).inflate(R.layout.item_gallery, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val path = images[position]

        Picasso.get()
            .load(File(path))
            .resize(THUMB_SIZE, THUMB_SIZE)
            .centerCrop()
            .into(holder.imageView)

        holder.imageView.tag = position
        holder.imageView.setOnClickListener {
            click.onNext(images[it.tag as Int])
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView = view.imageView
    }
}