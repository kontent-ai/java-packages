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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.github.kentico.delivery_android_sample.R;
import com.github.kentico.delivery_android_sample.app.articles.ArticlesActivity;
import com.github.kentico.delivery_android_sample.app.cafes.CafesActivity;
import com.github.kentico.delivery_android_sample.app.coffees.CoffeesActivity;
import com.github.kentico.delivery_android_sample.app.test.TestActivity;
import com.github.kentico.delivery_android_sample.util.NetworkHelper;

public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Drawer layout
     */
    protected DrawerLayout drawerLayout;

    /**
     * Network helper
     */
    protected NetworkHelper networkHelper;

     /**
     * Implement to get activity specific layout
     * @return id of the layout
     */
    protected int getLayoutResourceId(){
        return -1;
    }

    /**
     * Gets resource to title
     * @return id of the title string
     */
    protected int getTitleResourceId(){
        return -1;
    }

    /**
     * Gets menu item id
     * @return if of the menu item
     */
    protected int getMenuItemId(){
        return -1;
    }

    /**
     * Indicates if back button is shown in navigation drawer instead of menu items
     */
    protected boolean useBackButton(){
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init android networking
        // TODO: seems like this is not required after all, verify and remove if possible
        //AndroidNetworking.initialize(getApplicationContext());

        // Init network helper
        this.networkHelper = NetworkHelper.getInstance();

        // Render proper layout for child activity
        setContentView(getLayoutResourceId());

        // Set title
        int titleResourceId = getTitleResourceId();
        if (titleResourceId >= 0){
            setTitle(this.getTitleResourceId());
        }
        else{
            // set empty title
            setTitle("");
        }

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();

        if (ab != null) {
            if (useBackButton()) {
                ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            } else {
                ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            }
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // Set up the navigation drawer.
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        // Refresh connection button
        AppCompatButton refreshConnectionACB = (AppCompatButton)findViewById(R.id.refreshConnectionACB);
        refreshConnectionACB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check connection again
                if (networkHelper.isNetworkAvailable(getApplicationContext())){
                    // reload activity
                    finish();
                    startActivity(getIntent());
                }
                else{
                    // network still not available
                    Snackbar.make(findViewById(android.R.id.content), R.string.error_network_not_available, Snackbar.LENGTH_LONG)
                        .show();
                }
            }
        });
    }

    protected void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if (menuItem.getItemId() == getMenuItemId()){
                            // do nothing, we are on this screen already
                        }
                        else{
                            switch (menuItem.getItemId()) {
                                case R.id.cafes:
                                    Intent cafesIntent = new Intent(navigationView.getContext(), CafesActivity.class);
                                    startActivity(cafesIntent);
                                    break;
                                case R.id.articles:
                                    Intent articlesIntent = new Intent(navigationView.getContext(), ArticlesActivity.class);
                                    startActivity(articlesIntent);
                                    break;
                                case R.id.coffees:
                                    Intent coffeesIntent = new Intent(navigationView.getContext(), CoffeesActivity.class);
                                    startActivity(coffeesIntent);
                                    break;
                                case R.id.test:
                                    Intent testIntent = new Intent(navigationView.getContext(), TestActivity.class);
                                    startActivity(testIntent);
                                    break;
                            }
                        }

                        // Close the navigation drawer when an item is selected.
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (useBackButton()){
            NavUtils.navigateUpFromSameTask(this);
            return false;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                this.drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showConnectionNotAvailable(){
        LinearLayout noConnectionLL = (LinearLayout) findViewById(R.id.noConnectionLL);
        noConnectionLL.setVisibility(View.VISIBLE);
    }
}
