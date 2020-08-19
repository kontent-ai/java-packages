/*
 * Copyright 2017 Kentico s.r.o. and Richard Sustek
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.kentico.delivery_android_sample.data.source.articles;

import android.content.Context;
import android.support.annotation.NonNull;
import com.github.kentico.delivery_android_sample.app.config.AppConfig;
import com.github.kentico.delivery_android_sample.data.models.Article;
import kentico.kontent.delivery.DeliveryClient;

public class ArticlesKontentSource implements ArticlesDataSource {

    private static ArticlesKontentSource INSTANCE;
    private static DeliveryClient client = new DeliveryClient(AppConfig.KONTENT_PROJECT_ID);


    public static ArticlesKontentSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ArticlesKontentSource();
        }
        return INSTANCE;
    }

    @Override
    public void getArticles(@NonNull final LoadArticlesCallback callback) {
        client.getItems(Article.class)
                .thenAccept(items -> {
                    if (items == null || items.size() == 0) {
                        callback.onDataNotAvailable();
                        return;
                    }
                    callback.onItemsLoaded(items);
                }).exceptionally(ex -> {
            callback.onError(ex);
            return null;

        });

//        this.deliveryService.<Article>items()
//                .type(Article.TYPE)
//                .getObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<DeliveryItemListingResponse<Article>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                    }
//
//                    @Override
//                    public void onNext(DeliveryItemListingResponse<Article> response) {
//                        List<Article> items = (response.getItems());
//
//                        if (items == null || items.size() == 0) {
//                            callback.onDataNotAvailable();
//                            return;
//                        }
//
//                        callback.onItemsLoaded(items);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        callback.onError(e);
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }

    @Override
    public void getArticle(@NonNull String codename, @NonNull final LoadArticleCallback callback) {

        client.getItem(codename, Article.class)
                .thenAccept(item -> {
                    if (item == null) {
                        callback.onDataNotAvailable();
                    }
                    callback.onItemLoaded(item);
                });

//        this.deliveryService.<Article>item(codename)
//                .getObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<DeliveryItemResponse<Article>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(DeliveryItemResponse<Article> response) {
//                        if (response.getItem() == null) {
//                            callback.onDataNotAvailable();
//                        }
//
//                        callback.onItemLoaded(response.getItem());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        callback.onError(e);
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }
}
