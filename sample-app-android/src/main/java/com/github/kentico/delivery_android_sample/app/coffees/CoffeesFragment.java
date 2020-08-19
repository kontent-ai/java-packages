/*
 * Copyright 2017 Kentico s.r.o. and Richard Sustek
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.kentico.delivery_android_sample.app.coffees;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.kentico.delivery_android_sample.R;
import com.github.kentico.delivery_android_sample.app.coffee_detail.CoffeeDetailActivity;
import com.github.kentico.delivery_android_sample.app.core.BaseFragment;
import com.github.kentico.delivery_android_sample.app.shared.CommunicationHub;
import com.github.kentico.delivery_android_sample.data.models.Coffee;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CoffeesFragment extends BaseFragment<CoffeesContract.Presenter> implements CoffeesContract.View{

    private CoffeesAdapter adapter;

    public CoffeesFragment() {
        // Requires empty public constructor
    }

    public static CoffeesFragment newInstance() {
        return new CoffeesFragment();
    }

    @Override
    protected int getFragmentId(){
        return R.layout.coffees_frag;
    }

    @Override
    protected int getViewId(){
        return R.id.coffeesLL;
    }

    @Override
    protected boolean hasScrollSwipeRefresh() {
        return true;
    }

    @Override
    protected void onScrollSwipeRefresh() {
        this.presenter.loadCoffees();
    }

    @Override
    protected View scrollUpChildView() {
        return this.root.findViewById(R.id.coffeesLV);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.adapter = new CoffeesAdapter(new ArrayList<Coffee>(0), coffeeItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set up articles view
        ListView listView = (ListView) this.root.findViewById(R.id.coffeesLV);
        listView.setAdapter(this.adapter);

        return this.root;
    }

    @Override
    public void showCoffees(List<Coffee> coffees) {
        this.adapter.replaceData(coffees);
        this.fragmentView.setVisibility(View.VISIBLE);
    }

    /**
     * Listener for clicks on items in the ListView.
     */
    CoffeeItemListener coffeeItemListener = new CoffeeItemListener() {
        @Override
        public void onCoffeeClick(Coffee clickedCoffee) {
            Intent coffeeDetailIntent = new Intent(getContext(), CoffeeDetailActivity.class);
            coffeeDetailIntent.putExtra(CommunicationHub.CoffeeCodename.toString(), clickedCoffee.getSystem().getCodename());
            startActivity(coffeeDetailIntent);
        }
    };

    private static class CoffeesAdapter extends BaseAdapter {

        private List<Coffee> _coffees;
        private CoffeeItemListener _coffeeItemListener;

        CoffeesAdapter(List<Coffee> coffees, CoffeeItemListener itemListener) {
            setList(coffees);
            _coffeeItemListener = itemListener;
        }

        void replaceData(List<Coffee> coffees) {
            setList(coffees);
            notifyDataSetChanged();
        }

        private void setList(List<Coffee> coffees) {
            _coffees = checkNotNull(coffees);
        }

        @Override
        public int getCount() {
            return _coffees.size();
        }

        @Override
        public Coffee getItem(int i) {
            return _coffees.get(i);
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
                rowView = inflater.inflate(R.layout.coffee_item, viewGroup, false);
            }

            final Coffee coffee = getItem(i);

            // title
            TextView productNameTV = (TextView) rowView.findViewById(R.id.coffeeProductNameTV);
            productNameTV.setText(coffee.getProductName());

            // image
            final ImageView imageIV = (ImageView) rowView.findViewById(R.id.coffeeImageIV);
            Picasso.with(viewGroup.getContext()).load(coffee.getImageUrl()).into(imageIV);


            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _coffeeItemListener.onCoffeeClick(coffee);
                }
            });

            return rowView;
        }

    }

    interface CoffeeItemListener {

        void onCoffeeClick(Coffee clickedCoffee);
    }
}