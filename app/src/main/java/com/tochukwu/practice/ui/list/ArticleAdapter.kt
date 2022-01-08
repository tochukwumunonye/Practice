package com.tochukwu.practice.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tochukwu.practice.databinding.ArticleItemBinding
import com.tochukwu.practice.model.Article
import com.tochukwu.practice.util.formatTitle
import com.tochukwu.practice.util.loadImageOrDefault
import com.tochukwu.practice.util.loadOrGone

class ArticleAdapter : PagingDataAdapter<Article, ArticleAdapter.ArticleViewHolder>(DIFF_CALLBACK){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position) ?: return
        holder.bind(article)
    }


    class ArticleViewHolder(private val binding: ArticleItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(articleItem : Article){
            binding.run{
                source.loadOrGone(articleItem.source.name)
                title.loadOrGone(articleItem.title.formatTitle())
                articleImage.loadImageOrDefault(articleItem.imgUrl)

                setOnClickListener(articleItem)
            }
        }

        private fun setOnClickListener(articleItem: Article) {
            binding.detailItem.setOnClickListener{view ->
                navigateToDetail(articleItem, view)
            }
        }

        private fun navigateToDetail(articleItem: Article, view: View) {
            val directions = ArticleListFragmentDirections.actionArticleListFragmentToArticleFragment(articleItem)
            view.findNavController().navigate(directions)
        }

        companion object{
            fun from(parent: ViewGroup) : ArticleViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ArticleItemBinding.inflate(layoutInflater, parent, false)
                return ArticleViewHolder(binding)
            }
        }


    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Article> =
            object : DiffUtil.ItemCallback<Article>() {
                override fun areItemsTheSame(oldItem: Article, newItem: Article) =
                    oldItem == newItem

                override fun areContentsTheSame(oldItem: Article, newItem: Article) =
                    oldItem.id == newItem.id
            }
    }
}
