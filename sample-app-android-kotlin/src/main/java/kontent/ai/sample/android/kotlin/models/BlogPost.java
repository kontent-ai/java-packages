package kontent.ai.sample.android.kotlin.models;

import java.lang.String;
import java.time.ZonedDateTime;
import java.util.List;
import kontent.ai.delivery.ContentItem;
import kontent.ai.delivery.ContentItemMapping;
import kontent.ai.delivery.ElementMapping;
import kontent.ai.delivery.System;
import kontent.ai.delivery.Taxonomy;

/**
 * This code was generated by a <a href="https://github.com/kontent-ai/java-packages/tree/master/delivery-sdk-generators">delivery-sdk-generators tool</a>
 *
 * Changes to this file may cause incorrect behavior and will be lost if the code is regenerated.
 * For further modifications of the class, create a separate file and extend this class.
 */
@ContentItemMapping("blog_post")
public class BlogPost {
  @ElementMapping("title")
  String title;

  @ElementMapping("lead_paragraph")
  String leadParagraph;

  @ElementMapping("content")
  String content;

  @ElementMapping("blog_categories")
  List<Taxonomy> blogCategories;

  @ElementMapping("official_publish_date")
  ZonedDateTime officialPublishDate;

  @ContentItemMapping("author")
  List<ContentItem> author;

  @ContentItemMapping("related_call_to_action__cta_")
  List<ContentItem> relatedCallToActionCta;

  @ElementMapping("url")
  String url;

  @ContentItemMapping("company")
  List<ContentItem> company;

  System system;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getLeadParagraph() {
    return leadParagraph;
  }

  public void setLeadParagraph(String leadParagraph) {
    this.leadParagraph = leadParagraph;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public List<Taxonomy> getBlogCategories() {
    return blogCategories;
  }

  public void setBlogCategories(List<Taxonomy> blogCategories) {
    this.blogCategories = blogCategories;
  }

  public ZonedDateTime getOfficialPublishDate() {
    return officialPublishDate;
  }

  public void setOfficialPublishDate(ZonedDateTime officialPublishDate) {
    this.officialPublishDate = officialPublishDate;
  }

  public List<ContentItem> getAuthor() {
    return author;
  }

  public void setAuthor(List<ContentItem> author) {
    this.author = author;
  }

  public List<ContentItem> getRelatedCallToActionCta() {
    return relatedCallToActionCta;
  }

  public void setRelatedCallToActionCta(List<ContentItem> relatedCallToActionCta) {
    this.relatedCallToActionCta = relatedCallToActionCta;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public List<ContentItem> getCompany() {
    return company;
  }

  public void setCompany(List<ContentItem> company) {
    this.company = company;
  }

  public System getSystem() {
    return system;
  }

  public void setSystem(System system) {
    this.system = system;
  }
}
