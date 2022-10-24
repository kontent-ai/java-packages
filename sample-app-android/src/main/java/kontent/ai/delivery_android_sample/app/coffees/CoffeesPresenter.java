/*
 * Copyright 2022 Kontent s.r.o.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package kontent.ai.delivery_android_sample.app.coffees;

import android.support.annotation.NonNull;

import kontent.ai.delivery_android_sample.data.models.Coffee;
import kontent.ai.delivery_android_sample.data.source.coffees.CoffeesDataSource;
import kontent.ai.delivery_android_sample.data.source.coffees.CoffeesRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

class CoffeesPresenter implements CoffeesContract.Presenter {

    private final CoffeesRepository repository;

    private final CoffeesContract.View view;

    CoffeesPresenter(@NonNull CoffeesRepository repository, @NonNull CoffeesContract.View view) {
        this.repository = checkNotNull(repository, "repository cannot be null");
        this.view = checkNotNull(view, "view cannot be null!");

        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        loadCoffees();
    }

    @Override
    public void loadCoffees() {
        this.view.setLoadingIndicator(true);

        this.repository.getCoffees(new CoffeesDataSource.LoadCoffeesCallback() {
            @Override
            public void onItemsLoaded(List<Coffee> coffees) {
                view.setLoadingIndicator(false);
                view.showCoffees(coffees);
            }

            @Override
            public void onDataNotAvailable() {
                view.showNoData(true);
            }

            @Override
            public void onError(Throwable e) {
                view.showLoadingError();
            }
        });
    }
}
