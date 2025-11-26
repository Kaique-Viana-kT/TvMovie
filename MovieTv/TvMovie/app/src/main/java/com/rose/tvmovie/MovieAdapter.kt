package com.rose.tvmovie

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.rose.tvmovie.model.Movie
import com.rose.tvmovie.util.DowloadImageTask

class MovieAdapter(
    private val movie: List<Movie>,
    @LayoutRes private val layoutId: Int,
    private val onItemClickListener: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movie.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movie[position]
        holder.bind(movie)
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(movie: Movie){
            val imageView: ImageView = itemView.findViewById(R.id.placeholder_img)
            imageView.setOnClickListener{
                onItemClickListener?.invoke(movie.id)
            }

            DowloadImageTask(object : DowloadImageTask.Callback{
                override fun onResult(bitmap: Bitmap) {
                    imageView.setImageBitmap(bitmap)
                }
            }).execute(movie.cover_url)

           // Picasso.get().load(movie.cover_url).into(imageView)
            //imageView.setImageResource(movie.cover_url)
        }
    }
}