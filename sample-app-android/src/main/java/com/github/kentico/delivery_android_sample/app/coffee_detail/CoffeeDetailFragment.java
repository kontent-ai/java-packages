/*
 * Copyright 2017 Kentico s.r.o. and Richard Sustek
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.kentico.delivery_android_sample.app.coffee_detail;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.github.kentico.delivery_android_sample.R;
import com.github.kentico.delivery_android_sample.app.core.BaseFragment;
import com.github.kentico.delivery_android_sample.data.models.Coffee;
import com.github.kentico.delivery_android_sample.util.Location.LocationHelper;
import com.github.kentico.delivery_android_sample.util.Location.LocationInfo;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class CoffeeDetailFragment extends BaseFragment<CoffeeDetailContract.Presenter> implements CoffeeDetailContract.View, OnMapReadyCallback  {

    private GoogleMap map;

    public CoffeeDetailFragment() {
        // Requires empty public constructor
    }

    public static CoffeeDetailFragment newInstance() {
        return new CoffeeDetailFragment();
    }

    @Override
    protected int getFragmentId(){
        return R.layout.coffee_detail_frag;
    }

    @Override
    protected int getViewId(){
        return R.id.coffeeDetailLL;
    }

    @Override
    protected boolean hasScrollSwipeRefresh() {
        return true;
    }

    @Override
    protected void onScrollSwipeRefresh() {
        this.presenter.loadCoffee();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // init map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return this.root;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void showCoffee(Coffee coffee) {
        View view = getView();

        if (view == null) {
            return;
        }

        // Update activity title
        getActivity().setTitle(coffee.getProductName());

        // image
        final ImageView teaserIV = (ImageView) view.findViewById(R.id.coffeeDetailTeaserIV);
        Picasso.with(view.getContext()).load(coffee.getImageUrl()).into(teaserIV);

        TextView teaserLineTV = (TextView) view.findViewById(R.id.coffeeDetailTeaserLineTV);
        teaserLineTV.setText(coffee.getCountry());

        TextView originsLineTV = (TextView) view.findViewById(R.id.coffeeDetailOriginsLineTV);
        String originsText = "Experience coffee from the '" + coffee.getFarm() + "' farm, made in '" + coffee.getAltitude() + "' altitude";
        originsLineTV.setText(originsText);

        TextView longDescriptionTV = (TextView) view.findViewById(R.id.coffeeDetailDescriptionTV);
        longDescriptionTV.setText(Html.fromHtml(coffee.getLongDescription(), Html.FROM_HTML_MODE_COMPACT));

        TextView varietyLineTV = (TextView) view.findViewById(R.id.coffeeDetailVarietyLineTV);
        String varietyText = "Available in: " + coffee.getVariety();
        varietyLineTV.setText(varietyText);

        // init marker
        LocationInfo cafeLocation = null;
        try {
            cafeLocation = LocationHelper.getLocationFromAddress(getContext(), "", " ", coffee.getCountry());

            if (cafeLocation != null){
                LatLng cafeLatLng = new LatLng(cafeLocation.getLattitude(), cafeLocation.getLongtitude());
                this.map.addMarker(new MarkerOptions().position(cafeLatLng).title("Coffee origin"));
                this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(cafeLatLng, 3));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        setLoadingIndicator(false);
        this.fragmentView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        this.map.getUiSettings().setZoomGesturesEnabled(true);
        // scroll not enabled because it does not play nice with scroll that is required
        // it also causes issues when map takes full screen
        this.map.getUiSettings().setScrollGesturesEnabled(false);

        this.map.getUiSettings().setMapToolbarEnabled(true);
    }
}
