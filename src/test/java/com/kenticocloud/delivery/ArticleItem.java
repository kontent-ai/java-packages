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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@ContentItemMapping("article")
public class ArticleItem {

    String title;

    System systemInformationObject;

    @ElementMapping("summary")
    String articleSummary;

    String randomValue;

    ZonedDateTime postDate;

    List<Asset> teaserImage;

    ArticleItem coffeeProcessingTechniques;

    @ContentItemMapping("origins_of_arabica_bourbon")
    ContentItem arabicaBourbonOrigin;

    List<ArticleItem> articleItems;

    List<ContentItem> allModularContent;

    Map<String, ArticleItem> articleItemMap;

    Map<String, ContentItem> allModularContentMap;

    List<String> randomStringList;

    List<String> randomStringListWithNoAccessors;

    List<String> randomStringListWithNoSetter;

    Map<String, String> randomStringMap;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public System getSystemInformationObject() {
        return systemInformationObject;
    }

    public void setSystemInformationObject(System systemInformationObject) {
        this.systemInformationObject = systemInformationObject;
    }

    public String getArticleSummary() {
        return articleSummary;
    }

    public void setArticleSummary(String articleSummary) {
        this.articleSummary = articleSummary;
    }

    public String getRandomValue() {
        return randomValue;
    }

    public void setRandomValue(String randomValue) {
        this.randomValue = randomValue;
    }

    public ZonedDateTime getPostDate() {
        return postDate;
    }

    public void setPostDate(ZonedDateTime postDate) {
        this.postDate = postDate;
    }

    public List<Asset> getTeaserImage() {
        return teaserImage;
    }

    public void setTeaserImage(List<Asset> teaserImage) {
        this.teaserImage = teaserImage;
    }

    public ArticleItem getCoffeeProcessingTechniques() {
        return coffeeProcessingTechniques;
    }

    public void setCoffeeProcessingTechniques(ArticleItem coffeeProcessingTechniques) {
        this.coffeeProcessingTechniques = coffeeProcessingTechniques;
    }

    public ContentItem getArabicaBourbonOrigin() {
        return arabicaBourbonOrigin;
    }

    public void setArabicaBourbonOrigin(ContentItem arabicaBourbonOrigin) {
        this.arabicaBourbonOrigin = arabicaBourbonOrigin;
    }

    public List<ArticleItem> getArticleItems() {
        return articleItems;
    }

    public void setArticleItems(List<ArticleItem> articleItems) {
        this.articleItems = articleItems;
    }

    public List<ContentItem> getAllModularContent() {
        return allModularContent;
    }

    public void setAllModularContent(List<ContentItem> allModularContent) {
        this.allModularContent = allModularContent;
    }

    public Map<String, ArticleItem> getArticleItemMap() {
        return articleItemMap;
    }

    public void setArticleItemMap(Map<String, ArticleItem> articleItemMap) {
        this.articleItemMap = articleItemMap;
    }

    public Map<String, ContentItem> getAllModularContentMap() {
        return allModularContentMap;
    }

    public void setAllModularContentMap(Map<String, ContentItem> allModularContentMap) {
        this.allModularContentMap = allModularContentMap;
    }

    public List<String> getRandomStringList() {
        return randomStringList;
    }

    public void setRandomStringList(List<String> randomStringList) {
        this.randomStringList = randomStringList;
    }

    public List<String> getRandomStringListWithNoSetter() {
        return randomStringListWithNoSetter;
    }

    public Map<String, String> getRandomStringMap() {
        return randomStringMap;
    }

    public void setRandomStringMap(Map<String, String> randomStringMap) {
        this.randomStringMap = randomStringMap;
    }
}
