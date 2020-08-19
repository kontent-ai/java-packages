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

import android.os.Bundle;
import com.github.kentico.delivery_android_sample.R;
import com.github.kentico.delivery_android_sample.app.config.AppConfig;
import com.github.kentico.delivery_android_sample.app.core.BaseActivity;
import com.github.kentico.delivery_android_sample.util.ActivityUtils;
import kentico.kontent.delivery.DeliveryClient;

public class TestActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.main_layout;
    }

    @Override
    protected int getTitleResourceId() {
        return R.string.main_menu_test;
    }

    @Override
    protected int getMenuItemId() {
        return R.id.test;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check connection
        if (!this.networkHelper.isNetworkAvailable(this.getApplicationContext())){
            showConnectionNotAvailable();
            return;
        }

        // Set fragment
        TestFragment testFragment = (TestFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (testFragment == null) {
            // Create the fragment
            testFragment = TestFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), testFragment, R.id.contentFrame);
        }

        // create presenter
        new TestPresenter(new DeliveryClient(AppConfig.KONTENT_PROJECT_ID), testFragment);
    }
}

