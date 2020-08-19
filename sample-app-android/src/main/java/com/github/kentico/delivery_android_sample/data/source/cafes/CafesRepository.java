/*
 * Copyright 2017 Kentico s.r.o. and Richard Sustek
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.kentico.delivery_android_sample.data.source.cafes;

import android.support.annotation.NonNull;

import com.github.kentico.delivery_android_sample.data.models.Cafe;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CafesRepository implements CafesDataSource {

    private static CafesRepository INSTANCE = null;

    private final CafesDataSource dataSource;

    // Prevent direct instantiation.
    public CafesRepository(@NonNull CafesDataSource dataSource){
        this.dataSource = checkNotNull(dataSource);
    }

    @Override
    public void getCafes(@NonNull final LoadCafesCallback callback) {
        this.dataSource.getCafes(new LoadCafesCallback() {
            @Override
            public void onItemsLoaded(List<Cafe> cafes) {
                callback.onItemsLoaded(cafes);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }

            @Override
            public void onError(Throwable e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void getCafe(@NonNull String codename, @NonNull final LoadCafeCallback callback) {
        this.dataSource.getCafe(codename, new LoadCafeCallback() {
            @Override
            public void onItemLoaded(Cafe cafe) {
                callback.onItemLoaded(cafe);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }

            @Override
            public void onError(Throwable e) {
                callback.onError(e);
            }
        });
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param dataSource the backend data source
     * @return the {@link CafesRepository} instance
     */
    public static CafesRepository getInstance(CafesDataSource dataSource) {
        if (INSTANCE == null) {
            INSTANCE = new CafesRepository(dataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
