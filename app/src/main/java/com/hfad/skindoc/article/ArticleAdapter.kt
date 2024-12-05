package com.hfad.skindoc.article

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hfad.skindoc.R



class ArticleAdapter(
    private val context: Context,
    private val articles: List<ArticleModel>,
    private val onItemClick: (ArticleModel) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.article_card, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int = articles.size

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val linkTextView: TextView = itemView.findViewById(R.id.link)

        fun bind(article: ArticleModel) {
            linkTextView.text = article.link
            itemView.setOnClickListener {
                onItemClick(article)
            }
        }
    }
}
