package com.tochukwu.practice.ui.article

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.tochukwu.practice.R
import com.tochukwu.practice.databinding.ArticleFragmentBinding
import com.tochukwu.practice.model.Article
import com.tochukwu.practice.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleFragment : Fragment() {

    @VisibleForTesting
    private val args: ArticleFragmentArgs by navArgs()
    private val article: Article by lazy{
        args.selectedArticle
    }

    private val articleViewModel: ArticleViewModel by hiltNavGraphViewModels(R.id.navgraph)

    private  var _binding: ArticleFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = ArticleFragmentBinding.inflate(inflater, container, false)

        setupActionBar()
        setUpOpenWebsitesButton()

        articleViewModel.fetchArticle(article.id)

        articleViewModel.articleLiveData.observe(viewLifecycleOwner){article->
            displayArticle(article)
        }

        articleViewModel.shareArticleEvent.observe(viewLifecycleOwner, EventObserver{articleUrl->
            shareArticle(articleUrl)
        })
        articleViewModel.openWebsiteEvent.observe(viewLifecycleOwner, EventObserver { articleUrl ->
            openWebsite(articleUrl)
        })

        return binding.root
    }


    private fun setUpOpenWebsitesButton() {
       binding.article.continueReadingButton.setOnClickListener {
           articleViewModel.openWebsite(article.url)
       }
    }



    private fun displayArticle(selectedArticle: Article) {
        binding.apply{
            article.author.loadOrGone(selectedArticle.author)
            article.title.loadOrGone(selectedArticle.title.formatTitle())
            article.content.loadOrGone(selectedArticle.content.formatContent())
            article.description.loadOrGone(selectedArticle.description)
            article.date.loadOrGone(selectedArticle.date.formatDate())
            article.source.loadOrGone(selectedArticle.source.name)
            Glide.with(requireContext()).load(selectedArticle.imgUrl).into(articleImage)
        }
    }

    private fun setupActionBar() {
        binding.toolbar.apply{
            setTitle(R.string.app_name)
            navigationContentDescription = resources.getString(R.string.nav_up)
            setNavigationOnClickListener{
                findNavController().navigateUp()
            }

            setOnMenuItemClickListener{
                if(it.itemId == R.id.share)
                    articleViewModel.shareArticle(this@ArticleFragment.article.url)
                true
            }

        }
    }

    private fun shareArticle(article: String){
        val mimeType = "text/plain"
        ShareCompat.IntentBuilder(requireContext()).setType(mimeType).setChooserTitle(resources.getString(R.string.share_article))
            .setText(article)
            .startChooser()
    }

    private fun openWebsite(articleUrl: String) {


        val webPage: Uri = Uri.parse(articleUrl)
        val intent = Intent(Intent.ACTION_VIEW, webPage)
        if (intent.resolveActivity(requireContext().packageManager) != null)
            startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
