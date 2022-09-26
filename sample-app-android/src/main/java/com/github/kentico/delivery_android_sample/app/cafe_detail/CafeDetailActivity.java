/*
 * Copyright 2022 Kontent s.r.o.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.kentico.delivery_android_sample.app.cafe_detail;

import android.os.Bundle;
import com.github.kentico.delivery_android_sample.R;
import com.github.kentico.delivery_android_sample.app.core.BaseActivity;
import com.github.kentico.delivery_android_sample.app.shared.CommunicationHub;
import com.github.kentico.delivery_android_sample.data.source.cafes.CafesKontentSource;
import com.github.kentico.delivery_android_sample.data.source.cafes.CafesRepository;
import com.github.kentico.delivery_android_sample.util.ActivityUtils;

public class CafeDetailActivity extends BaseActivity{

    @Override
    protected int getLayoutResourceId() {
        return R.layout.main_layout;
    }

    @Override
    protected boolean useBackButton() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check connection
        if (!this.networkHelper.isNetworkAvailable(this.getApplicationContext())){
            showConnectionNotAvailable();
            return;
        }

        // get codename of the cafe from extra data
        String cafeCodename = getIntent().getStringExtra(CommunicationHub.CafeCodename.toString());

        // Set fragment
        CafeDetailFragment cafeDetailFragment = (CafeDetailFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (cafeDetailFragment == null) {
            // Create the fragment
            cafeDetailFragment = CafeDetailFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), cafeDetailFragment, R.id.contentFrame);
        }

        // create presenter
        new CafeDetailPresenter(CafesRepository.getInstance(CafesKontentSource.getInstance()), cafeDetailFragment, cafeCodename);
    }
}

