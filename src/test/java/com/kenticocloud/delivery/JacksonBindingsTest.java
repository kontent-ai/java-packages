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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class JacksonBindingsTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup(){
        objectMapper.registerModule(new JSR310Module());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void testContentListDeserialization() throws IOException {
        ContentItemsListingResponse response = objectMapper.readValue(
                this.getClass().getResource("SampleContentItemList.json"), ContentItemsListingResponse.class);
        Assert.assertNotNull("object failed deserialization", response);
        Assert.assertEquals(2, response.getModularContent().size());
        Pagination pagination = response.getPagination();
        Assert.assertNotNull(pagination);
        Assert.assertEquals(0, pagination.getSkip().intValue());
        Assert.assertEquals(3, pagination.getLimit().intValue());
        Assert.assertEquals(3, pagination.getCount().intValue());
        Assert.assertEquals(
                "https://deliver.kenticocloud.com/975bf280-fd91-488c-994c-2f04416e5ee3/items?system.type=article&elements=title%2csummary%2cpost_date%2cteaser_image&order=elements.post_date%5bdesc%5d&depth=0&limit=3&skip=3",
                pagination.getNextPage());
    }

    @Test
    public void testContentItemDeserialization() throws IOException {
        ContentItemResponse response = objectMapper.readValue(
                this.getClass().getResource("SampleContentItem.json"), ContentItemResponse.class);
        Assert.assertNotNull("object failed deserialization", response);
        Assert.assertEquals(2, response.getModularContent().size());

        ContentItem contentItem = response.getItem();
        Assert.assertNotNull(contentItem);

        System system = contentItem.getSystem();
        Assert.assertNotNull(system);
        Assert.assertEquals("f4b3fc05-e988-4dae-9ac1-a94aba566474", system.getId());
        Assert.assertEquals("On Roasts", system.getName());
        Assert.assertEquals("on_roasts", system.getCodename());
        Assert.assertEquals("default", system.getLanguage());
        Assert.assertEquals("article", system.getType());
        Assert.assertEquals(2016, system.getLastModified().getYear());
        Assert.assertEquals(1, system.getSitemapLocations().size());
        Assert.assertEquals("articles", system.getSitemapLocations().get(0));

        Assert.assertEquals(5, contentItem.elements.size());
        Element title = contentItem.elements.get("title");
        Assert.assertNotNull(title);
        Assert.assertEquals("text", title.getType());

        Assert.assertEquals("On Roasts", contentItem.getString("title"));
        Assert.assertNull(contentItem.getString("non_existent"));
        Assert.assertNull(contentItem.getString("post_date"));

        List<Asset> assets = contentItem.getAssets("teaser_image");
        Assert.assertEquals(1, assets.size());
        Assert.assertEquals("on-roasts-1080px.jpg", assets.get(0).getName());
        Assert.assertEquals(0, contentItem.getAssets("non_existent").size());
        Assert.assertEquals(0, contentItem.getAssets("post_date").size());

        Assert.assertNotNull(contentItem.getModularContent("coffee_processing_techniques"));
        Assert.assertNull(contentItem.getModularContent("non_existent"));
    }

    @Test
    public void testContentTypeListDeserialization() throws IOException {
        ContentTypesListingResponse response = objectMapper.readValue(
                this.getClass().getResource("SampleContentTypeList.json"), ContentTypesListingResponse.class);
        Assert.assertNotNull("object failed deserialization", response);

        Assert.assertEquals(2, response.getTypes().size());
        ContentType contentType = response.getTypes().get(0);
        Assert.assertNotNull(contentType);

        Pagination pagination = response.getPagination();
        Assert.assertNotNull(pagination);
    }

    @Test
    public void testContentTypeDeserialization() throws IOException {
        ContentType contentType = objectMapper.readValue(
                this.getClass().getResource("SampleContentType.json"), ContentType.class);
        Assert.assertNotNull("object failed deserialization", contentType);

        System system = contentType.getSystem();
        Assert.assertNotNull(system);
        Assert.assertEquals("929985ac-4aa5-436b-85a2-94c2d4fbbebd", system.getId());
        Assert.assertEquals("Coffee", system.getName());
        Assert.assertEquals("coffee", system.getCodename());
        Assert.assertEquals(2017, system.getLastModified().getYear());

        Assert.assertEquals(11, contentType.getElements().size());
        Element element = contentType.getElements().get("processing");
        Assert.assertNotNull(element);
        Assert.assertTrue(element instanceof MultipleChoiceElement);
        MultipleChoiceElement multipleChoiceElement = (MultipleChoiceElement) element;
        Assert.assertEquals("Processing", multipleChoiceElement.getName());
        Assert.assertEquals(3, multipleChoiceElement.getOptions().size());
        Option option = multipleChoiceElement.getOptions().get(0);
        Assert.assertEquals("Semi-dry", option.getName());
        Assert.assertEquals("semi_dry", option.getCodename());
    }

    @Test
    public void testTextElementDeserialization() throws IOException {
        TextElement textElement = objectMapper.readValue(
                this.getClass().getResource("SampleTextElement.json"), TextElement.class);
        Assert.assertNotNull("object failed deserialization", textElement);
        Assert.assertEquals("Meta keywords", textElement.getName());
        Assert.assertEquals("\"coffee beginner\", beverages", textElement.getValue());
    }

    @Test
    public void testRichTextElementDeserialization() throws IOException {
        RichTextElement richTextElement = objectMapper.readValue(
                this.getClass().getResource("SampleRichTextElement.json"), RichTextElement.class);
        Assert.assertNotNull("object failed deserialization", richTextElement);
        Assert.assertEquals("Description", richTextElement.getName());
        Assert.assertEquals(
                "<p>We operate our own roasteries, one on every continent we cover, from where we distribute right to the shops. This allows you to experience every cup as if you were right at the very farm it originated from. To achieve this, we use a refurbished 1920s Probat coffee roasters.</p>\n<figure data-image-id=\"14mio\"><img src=\"https://assets.kenticocloud.com:443/38af179c-40ba-42e7-a5ca-33b8cdcc0d45/237362b4-5f2b-480e-a1d3-0ad5a6d5f8bd/roaster.jpg\" alt=\"Roasting coffee beans\" data-image-id=\"14mio\"></figure><p>We know that roasting is something you must keep on constantly perfecting. Each coffee requires a different roast to get the aroma and taste just right. That’s why our experts fine tune the way we <a data-item-id=\"f4b3fc05-e988-4dae-9ac1-a94aba566474\" href=\"\">roast coffees</a> every day. It’s a constant struggle.</p><object type=\"application/kenticocloud\" data-type=\"item\" data-codename=\"coffee_processing_techniques\"></object>",
                richTextElement.getValue());
        Assert.assertEquals(1, richTextElement.getImages().size());
        Image image = richTextElement.getImages().get("14mio");
        Assert.assertNotNull(image);
        Assert.assertEquals("14mio", image.getImageId());
        Assert.assertEquals("Roasting coffee beans", image.getDescription());
        Assert.assertEquals(
                "https://assets.kenticocloud.com:443/38af179c-40ba-42e7-a5ca-33b8cdcc0d45/237362b4-5f2b-480e-a1d3-0ad5a6d5f8bd/roaster.jpg",
                image.getUrl());
        Assert.assertEquals(1, richTextElement.getLinks().size());
        Link link = richTextElement.getLinks().get("f4b3fc05-e988-4dae-9ac1-a94aba566474");
        Assert.assertEquals("article", link.getType());
        Assert.assertEquals("on_roasts", link.getCodename());
        Assert.assertEquals("", link.getUrlSlug());
        Assert.assertEquals(1, richTextElement.getModularContent().size());
        Assert.assertEquals("coffee_processing_techniques", richTextElement.getModularContent().get(0));
    }

    @Test
    public void testMultipleChoiceElementDeserialization() throws IOException {
        MultipleChoiceElement multipleChoiceElement = objectMapper.readValue(
                this.getClass().getResource("SampleMultipleChoiceElement.json"), MultipleChoiceElement.class);
        Assert.assertNotNull("object failed deserialization", multipleChoiceElement);
        Assert.assertEquals("Processing", multipleChoiceElement.getName());
        Assert.assertEquals(1, multipleChoiceElement.getValue().size());
        Option option = multipleChoiceElement.getValue().get(0);
        Assert.assertNotNull(option);
        Assert.assertEquals("Dry (Natural)", option.getName());
        Assert.assertEquals("dry__natural_", option.getCodename());
    }

    @Test
    public void testNumberElementDeserialization() throws IOException {
        NumberElement numberElement = objectMapper.readValue(
                this.getClass().getResource("SampleNumberElement.json"), NumberElement.class);
        Assert.assertNotNull("object failed deserialization", numberElement);
        Assert.assertEquals("Price", numberElement.getName());
        Assert.assertEquals(8.5, numberElement.getValue(), 0.000001);
    }

    @Test
    public void testDateTimeElementDeserialization() throws IOException {
        DateTimeElement dateTimeElement = objectMapper.readValue(
                this.getClass().getResource("SampleDateTimeElement.json"), DateTimeElement.class);
        Assert.assertNotNull("object failed deserialization", dateTimeElement);
        Assert.assertEquals("Post date", dateTimeElement.getName());
        Assert.assertEquals(2014, dateTimeElement.getValue().getYear());
        Assert.assertEquals(11, dateTimeElement.getValue().getMonthValue());
        Assert.assertEquals(18, dateTimeElement.getValue().getDayOfMonth());
        Assert.assertEquals(0, dateTimeElement.getValue().getHour());
        Assert.assertEquals(0, dateTimeElement.getValue().getMinute());
        Assert.assertEquals("UTC", dateTimeElement.getValue().getZone().getId());
    }

    @Test
    public void testAssetElementDeserialization() throws IOException {
        AssetsElement assetsElement = objectMapper.readValue(
                this.getClass().getResource("SampleAssetElement.json"), AssetsElement.class);
        Assert.assertNotNull("object failed deserialization", assetsElement);
        Assert.assertEquals("Teaser image", assetsElement.getName());
        Assert.assertEquals(1, assetsElement.getValue().size());
        Asset asset = assetsElement.getValue().get(0);
        Assert.assertNotNull(asset);
        Assert.assertEquals("coffee-beverages-explained-1080px.jpg", asset.getName());
        Assert.assertEquals("image/jpeg", asset.getType());
        Assert.assertEquals(90895, asset.getSize().longValue());
        Assert.assertNull(asset.getDescription());
        Assert.assertEquals(
                "https://assets.kenticocloud.com:443/38af179c-40ba-42e7-a5ca-33b8cdcc0d45/e700596b-03b0-4cee-ac5c-9212762c027a/coffee-beverages-explained-1080px.jpg",
                asset.getUrl());
    }

    @Test
    public void testModularContentElementDeserialization() throws IOException {
        ModularContentElement modularContentElement = objectMapper.readValue(
                this.getClass().getResource("SampleModularContentElement.json"), ModularContentElement.class);
        Assert.assertNotNull("object failed deserialization", modularContentElement);
        Assert.assertEquals("Facts", modularContentElement.getName());
        Assert.assertEquals(3, modularContentElement.getValue().size());
        Assert.assertEquals("our_philosophy", modularContentElement.getValue().get(0));
    }

    @Test
    public void testTaxonomyElementDeserialization() throws IOException {
        TaxonomyElement taxonomyElement = objectMapper.readValue(
                this.getClass().getResource("SampleTaxonomyElement.json"), TaxonomyElement.class);
        Assert.assertNotNull("object failed deserialization", taxonomyElement);
        Assert.assertEquals("Personas", taxonomyElement.getName());
        Assert.assertEquals("personas", taxonomyElement.getTaxonomyGroup());
        Assert.assertEquals(1, taxonomyElement.getValue().size());
        Taxonomy taxonomy = taxonomyElement.getValue().get(0);
        Assert.assertNotNull(taxonomy);
        Assert.assertEquals("Coffee lover", taxonomy.getName());
        Assert.assertEquals("coffee_lover", taxonomy.getCodename());
    }

    @Test
    public void testTaxonomyGroupListingResponse() throws IOException {
        TaxonomyGroupListingResponse response = objectMapper.readValue(
                this.getClass().getResource("SampleTaxonomyGroupListingResponse.json"), TaxonomyGroupListingResponse.class);
        Assert.assertNotNull("object failed deserialization", response);
        Assert.assertEquals(3, response.getTaxonomies().size());
        TaxonomyGroup group = response.getTaxonomies().get(0);
        Assert.assertEquals(2016, group.getSystem().getLastModified().getYear());
        Assert.assertEquals(2, group.getTerms().size());
        Assert.assertEquals("coffee_expert", group.getTerms().get(0).getCodename());
        Assert.assertEquals(2, group.getTerms().get(0).getTerms().size());
        Assert.assertNotNull(response.getPagination());
        Assert.assertEquals(3, response.getPagination().getLimit().intValue());
    }

    @Test
    public void testUrlSlugElementDeserialization() throws IOException {
        UrlSlugElement urlSlugElement = objectMapper.readValue(
                this.getClass().getResource("SampleUrlSlugElement.json"), UrlSlugElement.class);
        Assert.assertNotNull("object failed deserialization", urlSlugElement);
        Assert.assertEquals("URL slug", urlSlugElement.getName());
        Assert.assertEquals("brazil-natural-barra-grande", urlSlugElement.getValue());
    }

    @Test
    public void testKenticoErrorDeserialization() throws IOException {
        KenticoError kenticoError = objectMapper.readValue(
                this.getClass().getResource("SampleKenticoError.json"), KenticoError.class);
        Assert.assertNotNull("object failed deserialization", kenticoError);
        Assert.assertEquals("The requested content item 'on_roatst' was not found.", kenticoError.getMessage());
        Assert.assertEquals("HyufT6wUgEc=", kenticoError.getRequestId());
        Assert.assertEquals(100, kenticoError.getErrorCode());
        Assert.assertEquals(0, kenticoError.getSpecificCode());
    }
}
