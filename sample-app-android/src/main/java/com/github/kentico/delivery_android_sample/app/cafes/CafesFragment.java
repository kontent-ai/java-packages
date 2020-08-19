/*
 * Copyright 2017 Kentico s.r.o. and Richard Sustek
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.kentico.delivery_android_sample.app.cafes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.kentico.delivery_android_sample.R;
import com.github.kentico.delivery_android_sample.app.cafe_detail.CafeDetailActivity;
import com.github.kentico.delivery_android_sample.app.core.BaseFragment;
import com.github.kentico.delivery_android_sample.app.shared.CommunicationHub;
import com.github.kentico.delivery_android_sample.data.models.Cafe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CafesFragment extends BaseFragment<CafesContract.Presenter> implements CafesContract.View{

    private CafesAdapter adapter;

    public CafesFragment() {
        // Requires empty public constructor
    }

    public static CafesFragment newInstance() {
        return new CafesFragment();
    }

    @Override
    protected int getFragmentId(){
        return R.layout.cafes_frag;
    }

    @Override
    protected int getViewId(){
        return R.id.cafesLL;
    }

    @Override
    protected boolean hasScrollSwipeRefresh() {
        return true;
    }

    @Override
    protected void onScrollSwipeRefresh() {
        this.presenter.loadCafes();
    }

    @Override
    protected View scrollUpChildView() {
        return this.root.findViewById(R.id.cafesLV);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.adapter = new CafesAdapter(new ArrayList<Cafe>(0), cafeItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set up tasks view
        ListView listView = (ListView) this.root.findViewById(R.id.cafesLV);
        listView.setAdapter(this.adapter);

        return this.root;
    }

    @Override
    public void showCafes(List<Cafe> cafes) {
        this.adapter.replaceData(cafes);

        this.fragmentView.setVisibility(View.VISIBLE);
    }

    /**
     * Listener for clicks on cafes in the ListView.
     */
    CafeItemListener cafeItemListener = new CafeItemListener() {
        @Override
        public void onCafeClick(Cafe clickedCafe) {
            // to do some action when item is clicked
            Intent cafeDetailIntent = new Intent(getContext(), CafeDetailActivity.class);
            cafeDetailIntent.putExtra(CommunicationHub.CafeCodename.toString(), clickedCafe.getSystem().getCodename());
            startActivity(cafeDetailIntent);
        }
    };

    private static class CafesAdapter extends BaseAdapter {

        private List<Cafe> _cafes;
        private CafeItemListener _cafeItemListener;

        CafesAdapter(List<Cafe> cafes, CafeItemListener itemListener) {
            setList(cafes);
            _cafeItemListener = itemListener;
        }

        void replaceData(List<Cafe> cafes) {
            setList(cafes);
            notifyDataSetChanged();
        }

        private void setList(List<Cafe> cafes) {
            _cafes = checkNotNull(cafes);
        }

        @Override
        public int getCount() {
            return _cafes.size();
        }

        @Override
        public Cafe getItem(int i) {
            return _cafes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.cafe_item, viewGroup, false);
            }

            final Cafe cafe = getItem(i);

            // cafe properties
            TextView titleTV = (TextView) rowView.findViewById(R.id.cafeTitleTV);
            titleTV.setText(cafe.getCity());

            final ImageView teaserIV = (ImageView) rowView.findViewById(R.id.cafeTeaserIV);
            Picasso.with(viewGroup.getContext()).load(cafe.getPhotoUrl()).into(teaserIV);

            TextView streetLineTV = (TextView) rowView.findViewById(R.id.cafeStreetLineTV);
            streetLineTV.setText(cafe.getStreet());

            TextView cityLineTV = (TextView) rowView.findViewById(R.id.cafeCityLineTV);
            cityLineTV.setText(cafe.getZipCode() + ", " + cafe.getCity());

            TextView cafeCountryLineTV = (TextView) rowView.findViewById(R.id.cafeCountryLineTV);
            if (TextUtils.isEmpty(cafe.getState())){
                cafeCountryLineTV.setText(cafe.getCountry());
            }
            else{
                cafeCountryLineTV.setText(cafe.getCountry() + ", " + cafe.getState());
            }

            TextView cafePhoneLineTV = (TextView) rowView.findViewById(R.id.cafePhoneLineTV);
            cafePhoneLineTV.setText(cafe.getPhone());

            TextView cafeEmailLineTV = (TextView) rowView.findViewById(R.id.cafeEmailLineTV);
            cafeEmailLineTV.setText(cafe.getEmail());


            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _cafeItemListener.onCafeClick(cafe);
                }
            });

            return rowView;
        }
    }

    interface CafeItemListener {

        void onCafeClick(Cafe clickedCafe);
    }
}
