package com.rose.tvmovie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rose.tvmovie.model.Category

class CategoryAdapter(
    private val categories: List<Category>,
    private val onItemClickListiner: (Int) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_category, parent, false)
       return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(category: Category){
            val txtCategory: TextView = itemView.findViewById(R.id.text_category)
            txtCategory.text = category.name

            val rvCategory: RecyclerView = itemView.findViewById(R.id.rv_category)

            rvCategory.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            rvCategory.adapter = MovieAdapter(category.movies, R.layout.movie_item, onItemClickListiner)
        }
    }
}