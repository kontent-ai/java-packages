package kentico.kontent.delivery.sample.dancinggoat.app.articles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import kentico.kontent.delivery.sample.dancinggoat.R
import kentico.kontent.delivery.sample.dancinggoat.models.Article
import java.time.format.DateTimeFormatter

class ArticlesAdapter(private val context: Context,
                      private val dataSource: List<Article>) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val article = getItem(position) as Article
        val rowView = inflater.inflate(R.layout.list_item_article, parent, false)

        val thumbnailImageView = rowView.findViewById(R.id.articleTeaserIV) as ImageView
        val titleTextView = rowView.findViewById(R.id.articleTitleTV) as TextView
        val postDateTextView = rowView.findViewById(R.id.articlePostDateTV) as TextView
        val summaryTextView = rowView.findViewById(R.id.articleSummaryTV) as TextView

        Picasso.with(context).load(article.teaserImage[0].url).into(thumbnailImageView);
        titleTextView.text = article.title
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        postDateTextView.text = article.postDate.format(formatter)
        summaryTextView.text = article.summary

        return rowView
    }
}