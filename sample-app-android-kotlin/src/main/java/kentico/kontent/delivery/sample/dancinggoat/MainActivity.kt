package kentico.kontent.delivery.sample.dancinggoat

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.annotation.WorkerThread
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import kentico.kontent.delivery.*
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import java.util.function.BiConsumer
import java.util.function.Consumer

class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById<ListView>(R.id.articles_list_view);

        GlobalScope.launch(Dispatchers.Main) {
            val articles = withContext(Dispatchers.IO) { loadArticles() }
            displayArticles(articles);
        }
    }

    private fun displayArticles(articles: MutableList<ContentItem>) {
        val listItems = arrayOfNulls<String>(articles.size);
        for (i in 0 until articles.size) {
            val article = articles[i]
            listItems[i] = article.getString("title")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter
    }

    @WorkerThread
    private suspend fun loadArticles(): MutableList<ContentItem> {
        val client = DeliveryClient(DeliveryOptions("975bf280-fd91-488c-994c-2f04416e5ee3"), null)

        val params = DeliveryParameterBuilder.params().filterEquals("system.type", "article").build()

        val articles = client.getItems(params).await().items;
        return articles;
    }

}