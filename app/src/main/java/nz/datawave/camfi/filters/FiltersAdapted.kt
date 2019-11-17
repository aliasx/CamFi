package nz.datawave.camfi.filters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_gallery.view.*
import io.reactivex.subjects.Subject
import nz.datawave.camfi.R

class FilterAdapter(
    private val c: Context,
    private val images: ArrayList<Bitmap>,
    private val click: Subject<Int>
) : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    override fun getItemCount():Int = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(c).inflate(R.layout.item_gallery, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageBitmap(images[position])
        holder.imageView.tag = position
        holder.imageView.setOnClickListener {
            click.onNext(it.tag as Int)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView = view.imageView!!
    }
}