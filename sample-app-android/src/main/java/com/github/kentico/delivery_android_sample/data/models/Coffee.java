/*
 * Copyright 2017 Kentico s.r.o. and Richard Sustek
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.kentico.delivery_android_sample.data.models;

import com.github.kentico.kontent_delivery_core.elements.AssetsElement;
import com.github.kentico.kontent_delivery_core.elements.NumberElement;
import com.github.kentico.kontent_delivery_core.elements.RichTextElement;
import com.github.kentico.kontent_delivery_core.elements.TextElement;
import com.github.kentico.kontent_delivery_core.elements.models.AssetModel;
import com.github.kentico.kontent_delivery_core.models.item.ContentItem;
import com.github.kentico.kontent_delivery_core.models.item.ElementMapping;

public final class Coffee extends ContentItem{

    public static final String TYPE = "coffee";

    @ElementMapping("product_name")
    public TextElement productName;

    @ElementMapping("price")
    public NumberElement price;

    @ElementMapping("image")
    public AssetsElement image;

    @ElementMapping("short_description")
    public RichTextElement shortDescription;

    @ElementMapping("long_description")
    public RichTextElement longDescription;

    @ElementMapping("farm")
    public TextElement farm;

    @ElementMapping("country")
    public TextElement country;

    @ElementMapping("variety")
    public TextElement variety;

    @ElementMapping("altitude")
    public TextElement altitude;

    public String getProductName() { return productName.getValue(); }

    public double getPrice() { return price.getValue(); }

    public String getImageUrl() {
        AssetModel[] assets = image.getValue();

        if (assets == null){
            return null;
        }

        if (assets.length == 0){
            return null;
        }

        return assets[0].url;
    }

    public String getShortDescription() {
        return shortDescription.getValue();
    }

    public String getLongDescription() {
        return longDescription.getValue();
    }

    public String getFarm() {
        return farm.getValue();
    }

    public String getCountry() {
        return country.getValue();
    }

    public String getVariety() {
        return variety.getValue();
    }

    public String getAltitude() {
        return altitude.getValue();
    }

}
