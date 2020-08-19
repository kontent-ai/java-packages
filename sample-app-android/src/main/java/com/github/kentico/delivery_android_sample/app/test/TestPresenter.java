/*
 * Copyright 2017 Kentico s.r.o. and Richard Sustek
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.kentico.delivery_android_sample.app.test;

import android.support.annotation.NonNull;
import kentico.kontent.delivery.ContentItem;
import kentico.kontent.delivery.DeliveryClient;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

class TestPresenter implements TestContract.Presenter {

    private final TestContract.View view;
    private final DeliveryClient deliveryClient;

    TestPresenter(@NonNull DeliveryClient deliveryClient, @NonNull TestContract.View view) {
        this.view = checkNotNull(view, "view cannot be null!");
        this.deliveryClient = deliveryClient;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        loadData();
    }

    @Override
    public void loadData() {
        this.view.setLoadingIndicator(true);

        this.deliveryClient.getItems()
                .thenAccept(response -> {
                    List<ContentItem> items = (response.getItems());
                });
//        this.deliveryClient.type("coffee")
//                .getObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<DeliveryTypeResponse>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(DeliveryTypeResponse deliverySingleTypeResponse) {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//
//
//        this.deliveryClient.types()
//                .limitParameter(5)
//                .getObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<DeliveryTypeListingResponse>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(DeliveryTypeListingResponse deliveryTypeListingResponse) {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//
//        this.deliveryClient.<Cafe>items()
//                .type(Cafe.TYPE)
//                .getObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<DeliveryItemListingResponse<Cafe>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                    }
//
//                    @Override
//                    public void onNext(DeliveryItemListingResponse<Cafe> response) {
//                        List<Cafe> items = (response.getItems());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });


    }
}
