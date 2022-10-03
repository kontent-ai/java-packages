/*
 * Copyright 2022 Kontent s.r.o.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package kontent.ai.delivery_android_sample.data.source.coffees;

import android.support.annotation.NonNull;
import kontent.ai.delivery_android_sample.data.models.Coffee;
import kontent.ai.delivery_android_sample.data.source.DeliveryClientProvider;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kontent.ai.delivery.DeliveryClient;

import java.util.List;

public class CoffeesKontentSource implements CoffeesDataSource {

    private static CoffeesKontentSource INSTANCE;
    private static DeliveryClient client = DeliveryClientProvider.getClient();


    public static CoffeesKontentSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CoffeesKontentSource();
        }
        return INSTANCE;
    }

    @Override
    public void getCoffees(@NonNull final LoadCoffeesCallback callback) {
        Observable.fromCompletionStage(client.getItems(Coffee.class))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Coffee>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Coffee> coffees) {
                        if (coffees == null || coffees.size() == 0) {
                            callback.onDataNotAvailable();
                            return;
                        }

                        callback.onItemsLoaded(coffees);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void getCoffee(@NonNull String codename, @NonNull final LoadCoffeeCallback callback) {
        Observable.fromCompletionStage(client.getItem(codename, Coffee.class))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Coffee>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Coffee coffee) {
                        if (coffee == null) {
                            callback.onDataNotAvailable();
                        }

                        callback.onItemLoaded(coffee);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        callback.onError(e);

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
