/*
 * Copyright 2017 Kentico s.r.o. and Richard Sustek
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.kentico.delivery_android_sample.app.cafe_detail;

import android.support.annotation.NonNull;

import com.github.kentico.delivery_android_sample.data.models.Cafe;
import com.github.kentico.delivery_android_sample.data.source.cafes.CafesDataSource;
import com.github.kentico.delivery_android_sample.data.source.cafes.CafesRepository;

import static com.google.common.base.Preconditions.checkNotNull;

class CafeDetailPresenter implements CafeDetailContract.Presenter {

    private final String cafeCodename;
    private final CafesRepository repository;

    private final CafeDetailContract.View view;

    CafeDetailPresenter(@NonNull CafesRepository repository, @NonNull CafeDetailContract.View view, @NonNull String cafeCodename) {
        this.repository = checkNotNull(repository, "repository cannot be null");
        this.view = checkNotNull(view, "view cannot be null!");
        this.view.setPresenter(this);
        this.cafeCodename = cafeCodename;
    }

    @Override
    public void start() {
        loadCafe();
    }

    @Override
    public void loadCafe() {
        this.view.setLoadingIndicator(true);

        this.repository.getCafe(this.cafeCodename, new CafesDataSource.LoadCafeCallback() {

            @Override
            public void onItemLoaded(Cafe item) {
                view.showCafe(item);
            }

            @Override
            public void onDataNotAvailable() {
                view.showLoadingError();
            }

            @Override
            public void onError(Throwable e) {
                view.showLoadingError();
            }
        });
    }
}
