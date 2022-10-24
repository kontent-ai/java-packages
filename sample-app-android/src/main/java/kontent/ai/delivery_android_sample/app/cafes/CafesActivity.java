/*
 * Copyright 2022 Kontent s.r.o.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package kontent.ai.delivery_android_sample.app.cafes;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import kontent.ai.delivery_android_sample.R;
import kontent.ai.delivery_android_sample.app.core.BaseActivity;
import kontent.ai.delivery_android_sample.data.source.cafes.CafesKontentSource;
import kontent.ai.delivery_android_sample.data.source.cafes.CafesRepository;
import kontent.ai.delivery_android_sample.util.ActivityUtils;

public class CafesActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.main_layout;
    }

    @Override
    protected int getTitleResourceId() {
        return R.string.main_menu_cafes;
    }

    @Override
    protected int getMenuItemId() {
        return R.id.cafes;
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
        CafesFragment cafesFragment = (CafesFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (cafesFragment == null) {
            // Create the fragment
            cafesFragment = CafesFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), cafesFragment, R.id.contentFrame);
        }

        // create presenter
        new CafesPresenter(CafesRepository.getInstance(CafesKontentSource.getInstance()), cafesFragment);

    }
}

