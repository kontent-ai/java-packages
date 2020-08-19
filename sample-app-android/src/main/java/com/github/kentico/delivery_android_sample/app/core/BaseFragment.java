/*
 * Copyright 2017 Kentico s.r.o. and Richard Sustek
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.kentico.delivery_android_sample.app.core;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.kentico.delivery_android_sample.R;
import com.github.kentico.delivery_android_sample.app.shared.ScrollChildSwipeRefreshLayout;

import static com.google.common.base.Preconditions.checkNotNull;


public abstract class BaseFragment<TPresenter extends IBasePresenter> extends Fragment implements IBaseView<TPresenter>{

    protected View fragmentView;
    protected View root;
    protected TPresenter presenter;
    protected View noDataView;

    /***
     * Called when refreshing view scroll swipe
     * scroll is not initialized
     */
    protected void onScrollSwipeRefresh(){

    }

    /**
     * Fragment layout
     * @return id of the layout
     */
    protected abstract int getFragmentId();

    /**
     * Fragment view
     * @return id of the layout
     */
    protected abstract int getViewId();

    /**
     * indicates if fragment should init swipe refresh feature
     */
    protected boolean hasScrollSwipeRefresh(){
        return true;
    }

    /**
     * Use to set up custom layout for swipe scroll
     * @return id of the layout
     */
    protected View scrollUpChildView(){
        // by default the fragment layout is used
        return this.root.findViewById(getFragmentId());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.root = inflater.inflate(this.getFragmentId(), container, false);

        // Set fragment LL
        this.fragmentView = (View) this.root.findViewById(this.getViewId());

        setHasOptionsMenu(true);

        // Set up progress indicator
        if (this.hasScrollSwipeRefresh()) {
            final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                    (ScrollChildSwipeRefreshLayout) this.root.findViewById(R.id.refresh_layout);
            swipeRefreshLayout.setColorSchemeColors(
                    ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                    ContextCompat.getColor(getActivity(), R.color.colorAccent),
                    ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
            );
            // Set the scrolling view in the custom SwipeRefreshLayout.
            swipeRefreshLayout.setScrollUpChild(scrollUpChildView());

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    onScrollSwipeRefresh();
                }
            });
        }

        // set no data view
        this.noDataView = this.root.findViewById(R.id.noDataLL);

        return this.root;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.presenter.start();
    }

    @Override
    public void setPresenter(TPresenter presenter) {
        this.presenter = checkNotNull(presenter);
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }

        if (this.hasScrollSwipeRefresh()) {
            final SwipeRefreshLayout srl =
                    (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

            // Make sure setRefreshing() is called after the layout is done with everything else.
            srl.post(new Runnable() {
                @Override
                public void run() {
                    srl.setRefreshing(active);
                }
            });
        }
    }

    protected void showSnackbarMessage(String message) {
        View view = getView();

        if (view == null){
            return;
        }

        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showLoadingError() {
        showSnackbarMessage(getString(R.string.error_loading_data));
        setLoadingIndicator(false);
    }

    @Override
    public void showNoData(boolean show){
        if (show){
            this.noDataView.setVisibility(View.VISIBLE);
        }
        else{
            this.noDataView.setVisibility(View.GONE);
        }
    }
}
