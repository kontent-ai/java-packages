package kontent.ai.delivery.sample.dancinggoat.app.articles

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.WorkerThread
import android.widget.ListView
import kontent.ai.delivery.sample.dancinggoat.R
import kontent.ai.delivery.sample.dancinggoat.data.DeliveryClientProvider
import kontent.ai.delivery.sample.dancinggoat.models.Article
import kontent.ai.delivery.DeliveryParameterBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await

class ArticlesActivity : AppCompatActivity() {
    private lateinit var listView: ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.articles_activity)

        listView = findViewById<ListView>(R.id.articles_list_view);

        GlobalScope.launch(Dispatchers.Main) {
            val articles = withContext(Dispatchers.IO) { loadArticles() }
            displayArticles(articles);
        }
    }

    private fun displayArticles(articles: MutableList<Article>) {
        listView.adapter = ArticlesAdapter(this,  articles.toList())
    }

    @WorkerThread
    private suspend fun loadArticles(): MutableList<Article> {
        val client = DeliveryClientProvider.client;

        val params = DeliveryParameterBuilder.params().filterEquals("system.type", "article").build()
        return client.getItems(Article::class.java, params).await();
    }

}