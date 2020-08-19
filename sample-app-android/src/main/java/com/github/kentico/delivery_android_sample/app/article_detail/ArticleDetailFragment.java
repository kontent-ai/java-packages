/*
 * Copyright 2017 Kentico s.r.o. and Richard Sustek
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.kentico.delivery_android_sample.app.article_detail;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.kentico.delivery_android_sample.R;
import com.github.kentico.delivery_android_sample.app.core.BaseFragment;
import com.github.kentico.delivery_android_sample.data.models.Article;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ArticleDetailFragment extends BaseFragment<ArticleDetailContract.Presenter> implements ArticleDetailContract.View {

    public ArticleDetailFragment() {
        // Requires empty public constructor
    }

    public static ArticleDetailFragment newInstance() {
        return new ArticleDetailFragment();
    }

    @Override
    protected int getFragmentId(){
        return R.layout.article_detail_frag;
    }

    @Override
    protected int getViewId(){
        return R.id.articleDetailLL;
    }

    @Override
    protected boolean hasScrollSwipeRefresh() {
        return true;
    }

    @Override
    protected void onScrollSwipeRefresh() {
        this.presenter.loadArticle();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void showArticle(Article article) {
        View view = getView();

        if (view == null){
            return;
        }

        // Update activity title
        getActivity().setTitle(article.getTitle());

        // Set up article detail view
        TextView articleTitleTV = (TextView) view.findViewById(R.id.articleDetailTitleTV);
        articleTitleTV.setText(article.getTitle());
        setLoadingIndicator(false);

        // image
        final ImageView teaserIV = (ImageView) view.findViewById(R.id.articleDetailTeaserIV);
        Picasso.with(view.getContext()).load(article.getTeaserImageUrl()).into(teaserIV);

        // release date
        TextView postDateTV = (TextView) view.findViewById(R.id.articleDetailPostDateTV);
        SimpleDateFormat postDf = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        postDateTV.setText(postDf.format(article.getPostDate()));

        // text
        TextView bodyCopyTV = (TextView) view.findViewById(R.id.articleDetailBodyCopyTV);
        bodyCopyTV.setText(Html.fromHtml(article.getBodyCopy(), Html.FROM_HTML_MODE_COMPACT));

        this.fragmentView.setVisibility(View.VISIBLE);

    }
}
