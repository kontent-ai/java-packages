/*
 * MIT License
 *
 * Copyright (c) 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.kenticocloud.delivery;

import org.apache.http.NameValuePair;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class DeliveryClientTest {

    /*
    * This test is useful for looking at the real API
    */
    @Test
    @Ignore
    public void testRealApi() throws IOException {
        DeliveryClient client = new DeliveryClient("https://deliver.kenticocloud.com/", "b90eecb6-130d-4794-a3e9-f5b648defd87");

        List<NameValuePair> urlPattern = DeliveryParameterBuilder.params().filterEquals("elements.url_pattern", "/path1/path2/adams-article").build();
        ContentItemsListingResponse items = client.getItems(urlPattern);
        //Set debug breakpoint below to view model
        Assert.assertNotNull(items);
    }

    /*
    * This test is useful for looking at the real API
    */
    @Test
    @Ignore
    public void testRealPreviewApi() throws IOException {
        DeliveryClient client = new DeliveryClient("https://preview-deliver.kenticocloud.com/", "b90eecb6-130d-4794-a3e9-f5b648defd87", "ew0KICAiYWxnIjogIkhTMjU2IiwNCiAgInR5cCI6ICJKV1QiDQp9.ew0KICAidWlkIjogInVzcl8wdlRyODRzMnJGNnZRN3hQM1I0Q3pUIiwNCiAgImVtYWlsIjogImFkYW0ud2VpZ29sZEB0cnVzdGVkY2hvaWNlLmNvbSIsDQogICJwcm9qZWN0X2lkIjogImI5MGVlY2I2LTEzMGQtNDc5NC1hM2U5LWY1YjY0OGRlZmQ4NyIsDQogICJqdGkiOiAiYmV3WjlQX1VaYjRjblZ2NyIsDQogICJ2ZXIiOiAiMS4wLjAiLA0KICAiZ2l2ZW5fbmFtZSI6ICJBZGFtIiwNCiAgImZhbWlseV9uYW1lIjogIldlaWdvbGQiLA0KICAiYXVkIjogInByZXZpZXcuZGVsaXZlci5rZW50aWNvY2xvdWQuY29tIg0KfQ.WoiOWalz2njd4O47yl3vFlgZeXJb1iQpjVx80cs4OHM");
        ContentItemsListingResponse items = client.getItems();
        //Set debug breakpoint below to view model
        Assert.assertNotNull(items);
    }

    /*
    * This test is useful for looking at the real API
    */
    @Test
    @Ignore
    public void testRealGetContentItemApi() throws IOException {
        DeliveryClient client = new DeliveryClient("https://deliver.kenticocloud.com/", "b90eecb6-130d-4794-a3e9-f5b648defd87");
        //ContentItemResponse item = client.getItem("/path1/path2/adams-article");
        ContentItemResponse item = client.getItem("adam_s_article");
        //Set debug breakpoint below to view model
        Assert.assertNotNull(item);
    }

    /*
    * This test is useful for looking at the real API
    */
    @Test
    @Ignore
    public void testRealGetContentTypesApi() throws IOException {
        DeliveryClient client = new DeliveryClient("https://deliver.kenticocloud.com/", "b90eecb6-130d-4794-a3e9-f5b648defd87");
        //ContentItemResponse item = client.getItem("/path1/path2/adams-article");
        ContentTypesListingResponse types = client.getTypes();
        //Set debug breakpoint below to view model
        Assert.assertNotNull(types);
    }

    /*
    * This test is useful for looking at the real API
    */
    @Test
    @Ignore
    public void testRealGetContentTypeApi() throws IOException {
        DeliveryClient client = new DeliveryClient("https://deliver.kenticocloud.com/", "b90eecb6-130d-4794-a3e9-f5b648defd87");
        //ContentItemResponse item = client.getItem("/path1/path2/adams-article");
        ContentType type = client.getType("article");
        //Set debug breakpoint below to view model
        Assert.assertNotNull(type);
    }

    /*
    * This test is useful for looking at the real API
    */
    @Test
    @Ignore
    public void testRealGetContentTypeElementApi() throws IOException {
        DeliveryClient client = new DeliveryClient("https://deliver.kenticocloud.com/", "b90eecb6-130d-4794-a3e9-f5b648defd87");
        //ContentItemResponse item = client.getItem("/path1/path2/adams-article");
        Element element = client.getContentTypeElement("article", "title");
        //Set debug breakpoint below to view model
        Assert.assertNotNull(element);
    }

}
