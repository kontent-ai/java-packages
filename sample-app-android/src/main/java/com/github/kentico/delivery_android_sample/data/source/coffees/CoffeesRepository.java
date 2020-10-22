/*
 * Copyright 2017 Kentico s.r.o. and Richard Sustek
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.kentico.delivery_android_sample.data.source.coffees;

import android.support.annotation.NonNull;

import com.github.kentico.delivery_android_sample.data.models.Coffee;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CoffeesRepository implements CoffeesDataSource {

    private static CoffeesRepository INSTANCE = null;

    private final CoffeesDataSource dataSource;

    // Prevent direct instantiation.
    private CoffeesRepository(@NonNull CoffeesDataSource dataSource){
        this.dataSource = checkNotNull(dataSource);
    }

    @Override
    public void getCoffees(@NonNull final LoadCoffeesCallback callback) {
        this.dataSource.getCoffees(new LoadCoffeesCallback() {
            @Override
            public void onItemsLoaded(List<Coffee> coffees) {
                callback.onItemsLoaded(coffees);
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
    public void getCoffee(@NonNull String codename, @NonNull final LoadCoffeeCallback callback) {
        this.dataSource.getCoffee(codename, new LoadCoffeeCallback() {
            @Override
            public void onItemLoaded(Coffee coffee) {
                callback.onItemLoaded(coffee);
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
     * @return the {@link CoffeesRepository} instance
     */
    public static CoffeesRepository getInstance(CoffeesDataSource dataSource) {
        if (INSTANCE == null) {
            INSTANCE = new CoffeesRepository(dataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
