/*
 * MIT License
 *
 * Copyright (c) 2019
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

package kontent.ai.delivery;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import kontent.ai.delivery.*;
import kontent.ai.delivery.System;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class JacksonBindingsTest {

    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Before
    public void setup(){
        objectMapper.registerModule(new JSR310Module());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void testContentListDeserialization() throws IOException {
        ContentItemsListingResponse response = objectMapper.readValue(
                this.getClass().getResource("SampleContentItemList.json"), ContentItemsListingResponse.class);
        ContentItemsListingResponse response2 = objectMapper.readValue(
                this.getClass().getResource("SampleContentItemList.json"), ContentItemsListingResponse.class);
        Assert.assertNotNull("object failed deserialization", response);
        Assert.assertNotNull(response.toString());
        Assert.assertEquals(response, response2);
        Assert.assertEquals(response.hashCode(), response2.hashCode());
        ContentItemsListingResponse response3 = ContentItemsListingResponse.builder()
                .items(response.getItems())
                .pagination(response.getPagination())
                .linkedItems(response.getLinkedItems())
                .build();
        Assert.assertEquals(response, response3);
        Assert.assertEquals(response.hashCode(), response3.hashCode());

        Assert.assertEquals(2, response.getLinkedItems().size());
        Pagination pagination = response.getPagination();
        Assert.assertNotNull(pagination);
        Assert.assertNotNull(pagination.toString());
        Assert.assertEquals(pagination, response2.getPagination());
        Assert.assertEquals(pagination.hashCode(), response2.getPagination().hashCode());
        Assert.assertEquals(0, pagination.getSkip().intValue());
        Assert.assertEquals(3, pagination.getLimit().intValue());
        Assert.assertEquals(3, pagination.getCount().intValue());
        Assert.assertEquals(6, pagination.getTotalCount().intValue());
        Assert.assertEquals(
                "https://deliver.kontent.ai/975bf280-fd91-488c-994c-2f04416e5ee3/items?system.type=article&elements=title%2csummary%2cpost_date%2cteaser_image&order=elements.post_date%5bdesc%5d&depth=0&includeTotalCount=true&limit=3&skip=3",
                pagination.getNextPage());
    }

    @Test
    public void testContentItemDeserialization() throws IOException {
        ContentItemResponse response = objectMapper.readValue(
                this.getClass().getResource("SampleContentItem.json"), ContentItemResponse.class);
        ContentItemResponse response2 = objectMapper.readValue(
                this.getClass().getResource("SampleContentItem.json"), ContentItemResponse.class);
        Assert.assertNotNull("object failed deserialization", response);
        Assert.assertNotNull(response.toString());
        Assert.assertEquals(response, response2);
        Assert.assertEquals(response.hashCode(), response2.hashCode());
        ContentItemResponse response3 = ContentItemResponse.builder()
                .item(response.getItem())
                .linkedItems(response.getLinkedItems())
                .build();
        Assert.assertEquals(response, response3);
        Assert.assertEquals(response.hashCode(), response3.hashCode());

        Assert.assertEquals(3, response.getLinkedItems().size());

        ContentItem contentItem = response.getItem();
        Assert.assertNotNull(contentItem);
        ContentItem contentItem2 = ContentItem.builder()
                .system(contentItem.getSystem())
                .elements(contentItem.getElements())
                .build();
        Assert.assertEquals(contentItem, contentItem2);
        Assert.assertEquals(contentItem.hashCode(), contentItem2.hashCode());

        kontent.ai.delivery.System system = contentItem.getSystem();
        Assert.assertNotNull(system);
        Assert.assertEquals("f4b3fc05-e988-4dae-9ac1-a94aba566474", system.getId());
        Assert.assertEquals("On Roasts", system.getName());
        Assert.assertEquals("on_roasts", system.getCodename());
        Assert.assertEquals("default", system.getLanguage());
        Assert.assertEquals("article", system.getType());
        Assert.assertEquals("default", system.getCollection());
        Assert.assertEquals(2016, system.getLastModified().getYear());
        Assert.assertEquals(1, system.getSitemapLocations().size());
        Assert.assertEquals("articles", system.getSitemapLocations().get(0));
        Assert.assertEquals("published", system.getWorkflowStep());

        Assert.assertEquals(7, contentItem.elements.size());
        Element title = contentItem.elements.get("title");
        Assert.assertNotNull(title);
        Assert.assertNotNull(title.toString());
        Assert.assertEquals("text", title.getType());

        Assert.assertEquals("On Roasts", contentItem.getString("title"));
        Assert.assertNull(contentItem.getString("non_existent"));
        Assert.assertNull(contentItem.getString("post_date"));

        List<Asset> assets = contentItem.getAssets("teaser_image");
        Assert.assertEquals(1, assets.size());
        Assert.assertEquals("on-roasts-1080px.jpg", assets.get(0).getName());
        Assert.assertEquals(0, contentItem.getAssets("non_existent").size());
        Assert.assertEquals(0, contentItem.getAssets("post_date").size());

        Assert.assertNotNull(contentItem.getLinkedItem("coffee_processing_techniques"));
        Assert.assertNotNull(contentItem.getLinkedItem("origins_of_arabica_bourbon"));
        Assert.assertNotNull(contentItem.getLinkedItem("component_child"));
        Assert.assertNull(contentItem.getLinkedItem("non_existent"));

        // Testing that Component does not have a `workflow_step`
        Assert.assertNotNull(contentItem.getLinkedItem("component_child").getSystem());
        Assert.assertNull(contentItem.getLinkedItem("component_child").getSystem().getWorkflowStep());

        Element themeColor = contentItem.elements.get("theme_color");
        Assert.assertNotNull(themeColor);
        Assert.assertNotNull(themeColor.toString());
        Assert.assertEquals("custom", themeColor.getType());
        Assert.assertEquals("#ff0000", themeColor.getValue());
    }

    @Test
    public void testContentItemSerialization() throws IOException {
        ContentItemResponse response = objectMapper.readValue(
                this.getClass().getResource("SampleContentItem.json"), ContentItemResponse.class);

        String serializedContentItemResponse = objectMapper.writeValueAsString(response);

        ContentItemResponse response2 = objectMapper.readValue(
                serializedContentItemResponse, ContentItemResponse.class);

        Assert.assertEquals(response, response2);
    }

    @Test
    public void testContentTypeListDeserialization() throws IOException {
        ContentTypesListingResponse response = objectMapper.readValue(
                this.getClass().getResource("SampleContentTypeList.json"), ContentTypesListingResponse.class);
        ContentTypesListingResponse response2 = objectMapper.readValue(
                this.getClass().getResource("SampleContentTypeList.json"), ContentTypesListingResponse.class);
        Assert.assertNotNull("object failed deserialization", response);
        Assert.assertNotNull(response.toString());
        Assert.assertEquals(response, response2);
        Assert.assertEquals(response.hashCode(), response2.hashCode());
        ContentTypesListingResponse response3 = ContentTypesListingResponse.builder()
                .types(response.getTypes())
                .pagination(response.getPagination())
                .build();
        Assert.assertEquals(response, response3);
        Assert.assertEquals(response.hashCode(), response3.hashCode());

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
        ContentType contentType2 = objectMapper.readValue(
                this.getClass().getResource("SampleContentType.json"), ContentType.class);
        Assert.assertNotNull("object failed deserialization", contentType);
        Assert.assertNotNull(contentType.toString());
        Assert.assertEquals(contentType, contentType2);
        Assert.assertEquals(contentType.hashCode(), contentType2.hashCode());
        ContentType contentType3 = ContentType.builder()
                .system(contentType.getSystem())
                .elements(contentType.getElements())
                .build();
        Assert.assertEquals(contentType, contentType3);
        Assert.assertEquals(contentType.hashCode(), contentType3.hashCode());

        System system = contentType.getSystem();
        Assert.assertNotNull(system);
        Assert.assertEquals("929985ac-4aa5-436b-85a2-94c2d4fbbebd", system.getId());
        Assert.assertEquals("Coffee", system.getName());
        Assert.assertEquals("coffee", system.getCodename());
        Assert.assertEquals(2017, system.getLastModified().getYear());
        Assert.assertNull(system.getLanguage());
        Assert.assertNull(system.getCollection());
        Assert.assertNull(system.getType());
        Assert.assertNull(system.getSitemapLocations());
        Assert.assertNull(system.getWorkflowStep());

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
        TextElement textElement2 = objectMapper.readValue(
                this.getClass().getResource("SampleTextElement.json"), TextElement.class);
        Assert.assertNotNull("object failed deserialization", textElement);
        Assert.assertNotNull(textElement.toString());
        Assert.assertEquals(textElement, textElement2);
        Assert.assertEquals(textElement.hashCode(), textElement2.hashCode());
        Assert.assertEquals("Meta keywords", textElement.getName());
        Assert.assertEquals("\"coffee beginner\", beverages", textElement.getValue());
    }

    @Test
    public void testRichTextElementDeserialization() throws IOException {
        RichTextElement richTextElement = objectMapper.readValue(
                this.getClass().getResource("SampleRichTextElement.json"), RichTextElement.class);
        RichTextElement richTextElement2 = objectMapper.readValue(
                this.getClass().getResource("SampleRichTextElement.json"), RichTextElement.class);
        Assert.assertNotNull("object failed deserialization", richTextElement);
        Assert.assertNotNull(richTextElement.toString());
        Assert.assertEquals(richTextElement, richTextElement2);
        Assert.assertEquals(richTextElement.hashCode(), richTextElement2.hashCode());
        Assert.assertEquals("Description", richTextElement.getName());
        Assert.assertEquals(
                "<p>We operate our own roasteries, one on every continent we cover, from where we distribute right to the shops. This allows you to experience every cup as if you were right at the very farm it originated from. To achieve this, we use a refurbished 1920s Probat coffee roasters.</p>\n<figure data-image-id=\"14mio\"><img src=\"https://assets-us-01.kc-usercontent.com:443/38af179c-40ba-42e7-a5ca-33b8cdcc0d45/237362b4-5f2b-480e-a1d3-0ad5a6d5f8bd/roaster.jpg\" alt=\"Roasting coffee beans\" data-image-id=\"14mio\"></figure><p>We know that roasting is something you must keep on constantly perfecting. Each coffee requires a different roast to get the aroma and taste just right. That's why our experts fine tune the way we <a data-item-id=\"f4b3fc05-e988-4dae-9ac1-a94aba566474\" href=\"\">roast coffees</a> every day. It's a constant struggle.</p><object type=\"application/kenticocloud\" data-type=\"item\" data-codename=\"coffee_processing_techniques\"></object>",
                richTextElement.getValue());
        Assert.assertEquals(1, richTextElement.getImages().size());
        Image image = richTextElement.getImages().get("14mio");
        Assert.assertNotNull(image);
        Assert.assertEquals("14mio", image.getImageId());
        Assert.assertEquals("Roasting coffee beans", image.getDescription());
        Assert.assertEquals(
                "https://assets-us-01.kc-usercontent.com/38af179c-40ba-42e7-a5ca-33b8cdcc0d45/237362b4-5f2b-480e-a1d3-0ad5a6d5f8bd/roaster.jpg",
                image.getUrl());
        Assert.assertEquals(1, richTextElement.getLinks().size());
        Link link = richTextElement.getLinks().get("f4b3fc05-e988-4dae-9ac1-a94aba566474");
        Assert.assertEquals("article", link.getType());
        Assert.assertEquals("on_roasts", link.getCodename());
        Assert.assertEquals("", link.getUrlSlug());
        Assert.assertEquals(1, richTextElement.getLinkedItems().size());
        Assert.assertEquals("coffee_processing_techniques", richTextElement.getLinkedItems().get(0));
    }

    @Test
    public void testMultipleChoiceElementDeserialization() throws IOException {
        MultipleChoiceElement multipleChoiceElement = objectMapper.readValue(
                this.getClass().getResource("SampleMultipleChoiceElement.json"), MultipleChoiceElement.class);
        MultipleChoiceElement multipleChoiceElement2 = objectMapper.readValue(
                this.getClass().getResource("SampleMultipleChoiceElement.json"), MultipleChoiceElement.class);
        Assert.assertNotNull("object failed deserialization", multipleChoiceElement);
        Assert.assertNotNull(multipleChoiceElement.toString());
        Assert.assertEquals(multipleChoiceElement, multipleChoiceElement2);
        Assert.assertEquals(multipleChoiceElement.hashCode(), multipleChoiceElement2.hashCode());
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
        NumberElement numberElement2 = objectMapper.readValue(
                this.getClass().getResource("SampleNumberElement.json"), NumberElement.class);
        Assert.assertNotNull("object failed deserialization", numberElement);
        Assert.assertNotNull(numberElement.toString());
        Assert.assertEquals(numberElement, numberElement2);
        Assert.assertEquals(numberElement.hashCode(), numberElement2.hashCode());
        Assert.assertEquals("Price", numberElement.getName());
        Assert.assertEquals(8.5, numberElement.getValue(), 0.000001);
    }

    @Test
    public void testDateTimeElementDeserialization() throws IOException {
        DateTimeElement dateTimeElement = objectMapper.readValue(
                this.getClass().getResource("SampleDateTimeElement.json"), DateTimeElement.class);
        DateTimeElement dateTimeElement2 = objectMapper.readValue(
                this.getClass().getResource("SampleDateTimeElement.json"), DateTimeElement.class);
        Assert.assertNotNull("object failed deserialization", dateTimeElement);
        Assert.assertNotNull(dateTimeElement.toString());
        Assert.assertEquals(dateTimeElement, dateTimeElement2);
        Assert.assertEquals(dateTimeElement.hashCode(), dateTimeElement2.hashCode());
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
        AssetsElement assetsElement2 = objectMapper.readValue(
                this.getClass().getResource("SampleAssetElement.json"), AssetsElement.class);
        Assert.assertNotNull("object failed deserialization", assetsElement);
        Assert.assertNotNull(assetsElement.toString());
        Assert.assertEquals(assetsElement, assetsElement2);
        Assert.assertEquals(assetsElement.hashCode(), assetsElement2.hashCode());
        Assert.assertEquals("Teaser image", assetsElement.getName());
        Assert.assertEquals(1, assetsElement.getValue().size());
        Asset asset = assetsElement.getValue().get(0);
        Assert.assertNotNull(asset);
        Asset asset2 = Asset.builder()
                .name(asset.getName())
                .description(asset.getDescription())
                .size(asset.getSize())
                .url(asset.getUrl())
                .type(asset.getType())
                .height(asset.getHeight())
                .width(asset.getWidth())
                .build();
        Assert.assertEquals(asset, asset2);
        Assert.assertEquals(asset.hashCode(), asset2.hashCode());
        Assert.assertEquals("coffee-beverages-explained-1080px.jpg", asset.getName());
        Assert.assertEquals("image/jpeg", asset.getType());
        Assert.assertEquals(90895, asset.getSize().longValue());
        Assert.assertNull(asset.getDescription());
        Assert.assertEquals(
                "https://assets-us-01.kc-usercontent.com:443/38af179c-40ba-42e7-a5ca-33b8cdcc0d45/e700596b-03b0-4cee-ac5c-9212762c027a/coffee-beverages-explained-1080px.jpg",
                asset.getUrl());
    }

    @Test
    public void testLinkedItemsElementDeserialization() throws IOException {
        LinkedItem linkedItem = objectMapper.readValue(
                this.getClass().getResource("SampleLinkedItem.json"), LinkedItem.class);
        LinkedItem linkedItem1 = objectMapper.readValue(
                this.getClass().getResource("SampleLinkedItem.json"), LinkedItem.class);
        Assert.assertNotNull("object failed deserialization", linkedItem);
        Assert.assertNotNull(linkedItem.toString());
        Assert.assertEquals(linkedItem, linkedItem1);
        Assert.assertEquals(linkedItem.hashCode(), linkedItem1.hashCode());
        Assert.assertEquals("Facts", linkedItem.getName());
        Assert.assertEquals(3, linkedItem.getValue().size());
        Assert.assertEquals("our_philosophy", linkedItem.getValue().get(0));
    }

    @Test
    public void testTaxonomyElementDeserialization() throws IOException {
        TaxonomyElement taxonomyElement = objectMapper.readValue(
                this.getClass().getResource("SampleTaxonomyElement.json"), TaxonomyElement.class);
        TaxonomyElement taxonomyElement2 = objectMapper.readValue(
                this.getClass().getResource("SampleTaxonomyElement.json"), TaxonomyElement.class);
        Assert.assertNotNull("object failed deserialization", taxonomyElement);
        Assert.assertNotNull(taxonomyElement.toString());
        Assert.assertEquals(taxonomyElement, taxonomyElement2);
        Assert.assertEquals(taxonomyElement.hashCode(), taxonomyElement2.hashCode());
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
        TaxonomyGroupListingResponse response2 = objectMapper.readValue(
                this.getClass().getResource("SampleTaxonomyGroupListingResponse.json"), TaxonomyGroupListingResponse.class);
        Assert.assertNotNull("object failed deserialization", response);
        Assert.assertNotNull(response.toString());
        Assert.assertEquals(response, response2);
        Assert.assertEquals(response.hashCode(), response2.hashCode());
        TaxonomyGroupListingResponse response3 = TaxonomyGroupListingResponse.builder()
                .taxonomies(response.getTaxonomies())
                .pagination(response.getPagination())
                .build();
        Assert.assertEquals(response, response3);
        Assert.assertEquals(response.hashCode(), response3.hashCode());

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
        UrlSlugElement urlSlugElement2 = objectMapper.readValue(
                this.getClass().getResource("SampleUrlSlugElement.json"), UrlSlugElement.class);
        Assert.assertNotNull("object failed deserialization", urlSlugElement);
        Assert.assertNotNull(urlSlugElement.toString());
        Assert.assertEquals(urlSlugElement, urlSlugElement2);
        Assert.assertEquals(urlSlugElement.hashCode(), urlSlugElement2.hashCode());
        Assert.assertEquals("URL slug", urlSlugElement.getName());
        Assert.assertEquals("brazil-natural-barra-grande", urlSlugElement.getValue());
    }

    @Test
    public void testCustomElementDeserialization() throws IOException {
        CustomElement textElement = objectMapper.readValue(
                this.getClass().getResource("SampleCustomElement.json"), CustomElement.class);
        CustomElement textElement2 = objectMapper.readValue(
                this.getClass().getResource("SampleCustomElement.json"), CustomElement.class);
        Assert.assertNotNull("object failed deserialization", textElement);
        Assert.assertNotNull(textElement.toString());
        Assert.assertEquals(textElement, textElement2);
        Assert.assertEquals(textElement.hashCode(), textElement2.hashCode());
        Assert.assertEquals("Some Custom Element", textElement.getName());
        Assert.assertEquals("custom element value", textElement.getValue());
    }

    @Test
    public void testKontentErrorDeserialization() throws IOException {
        KontentError kontentError = objectMapper.readValue(
                this.getClass().getResource("SampleKontentError.json"), KontentError.class);
        KontentError kontentError2 = objectMapper.readValue(
                this.getClass().getResource("SampleKontentError.json"), KontentError.class);
        Assert.assertNotNull("object failed deserialization", kontentError);
        Assert.assertNotNull(kontentError.toString());
        Assert.assertEquals(kontentError, kontentError2);
        Assert.assertEquals(kontentError.hashCode(), kontentError2.hashCode());
        Assert.assertEquals("The requested content item 'error' was not found.", kontentError.getMessage());
        Assert.assertEquals("HyufT6wUgEc=", kontentError.getRequestId());
        Assert.assertEquals(100, kontentError.getErrorCode());
        Assert.assertEquals(0, kontentError.getSpecificCode());
    }
}
