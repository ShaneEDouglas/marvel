package com.example.marvel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class marveldapter(private val characters: MutableList<MarvelCharacter>) :
    RecyclerView.Adapter<marveldapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.mar_title)
        val tvDescription: TextView = itemView.findViewById(R.id.mardes)
        val ivThumbnail: ImageView = itemView.findViewById(R.id.marimg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.maritem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val character = characters[position]
        holder.tvName.text = character.name
        holder.tvDescription.text = character.description

        Glide.with(holder.itemView.context)
            .load(character.imageUrl)
            .into(holder.ivThumbnail)
    }

    override fun getItemCount() = characters.size

    fun updateData(newCharacters: List<MarvelCharacter>) {
        characters.clear()
        characters.addAll(newCharacters)
        notifyDataSetChanged()
    }
}