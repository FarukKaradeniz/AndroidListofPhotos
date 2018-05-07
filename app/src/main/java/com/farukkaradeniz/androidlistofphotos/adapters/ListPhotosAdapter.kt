package com.farukkaradeniz.androidlistofphotos.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.farukkaradeniz.androidlistofphotos.R
import com.farukkaradeniz.androidlistofphotos.extensions.setImage
import com.farukkaradeniz.androidlistofphotos.model.Photo
import kotlinx.android.synthetic.main.photo_item.view.*

/**
 * Created by Faruk Karadeniz on 7.05.2018.
 * Twitter: twitter.com/Omeerfk
 * Github: github.com/FarukKaradeniz
 * LinkedIn: linkedin.com/in/FarukKaradeniz
 * Website: farukkaradeniz.com
 */
class ListPhotosAdapter(private var items: MutableList<Photo>,
                        private val itemClick: (Photo) -> Unit)
    : RecyclerView.Adapter<ListPhotosViewHolder>() {

    //Viewholder olusturulur
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListPhotosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return ListPhotosViewHolder(view, itemClick)
    }

    override fun getItemCount() = items.size

    //Viewholder ile item bind edilir
    override fun onBindViewHolder(holder: ListPhotosViewHolder, position: Int) {
        holder.bindPhoto(items[position])
    }

    fun addData(newData: List<Photo>) {
        items.addAll(newData)
        notifyDataSetChanged()
    }

}

class ListPhotosViewHolder(itemView: View,
                           private val itemClick: (Photo) -> Unit)
    : RecyclerView.ViewHolder(itemView) {

    fun bindPhoto(photo: Photo) {
        with(photo) {
            itemView.tags_textview.text = photo.tags
            itemView.item_preview_photo.setImage(photo.previewURL)
            itemView.setOnClickListener { itemClick(this) }
        }
    }
}