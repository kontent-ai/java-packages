package kentico.kontent.delivery.sample.dancinggoat.models;

import java.lang.Double;
import java.lang.String;
import java.util.List;
import kontent.ai.delivery.Asset;
import kontent.ai.delivery.ContentItemMapping;
import kontent.ai.delivery.ElementMapping;
import kontent.ai.delivery.System;
import kontent.ai.delivery.Taxonomy;

/**
 * This code was generated by a <a href="https://github.com/Kentico/cloud-generators-java">cloud-generators-java tool</a>
 *
 * Changes to this file may cause incorrect behavior and will be lost if the code is regenerated.
 * For further modifications of the class, create a separate file and extend this class.
 */
@ContentItemMapping("coffee")
public class Coffee {
  @ElementMapping("metadata__og_description")
  String metadataOgDescription;

  @ElementMapping("metadata__meta_title")
  String metadataMetaTitle;

  @ElementMapping("metadata__og_title")
  String metadataOgTitle;

  @ElementMapping("product_status")
  List<Taxonomy> productStatus;

  @ElementMapping("altitude")
  String altitude;

  @ElementMapping("metadata__meta_description")
  String metadataMetaDescription;

  @ElementMapping("variety")
  String variety;

  @ElementMapping("image")
  List<Asset> image;

  @ElementMapping("metadata__twitter_site")
  String metadataTwitterSite;

  @ElementMapping("url_pattern")
  String urlPattern;

  @ElementMapping("price")
  Double price;

  @ElementMapping("metadata__twitter_image")
  List<Asset> metadataTwitterImage;

  @ElementMapping("metadata__twitter_creator")
  String metadataTwitterCreator;

  @ElementMapping("country")
  String country;

  @ElementMapping("sitemap")
  List<Taxonomy> sitemap;

  @ElementMapping("metadata__twitter_title")
  String metadataTwitterTitle;

  @ElementMapping("short_description")
  String shortDescription;

  @ElementMapping("processing")
  List<Taxonomy> processing;

  @ElementMapping("metadata__twitter_description")
  String metadataTwitterDescription;

  @ElementMapping("metadata__og_image")
  List<Asset> metadataOgImage;

  @ElementMapping("long_description")
  String longDescription;

  @ElementMapping("farm")
  String farm;

  @ElementMapping("product_name")
  String productName;

  System system;

  public String getMetadataOgDescription() {
    return metadataOgDescription;
  }

  public void setMetadataOgDescription(String metadataOgDescription) {
    this.metadataOgDescription = metadataOgDescription;
  }

  public String getMetadataMetaTitle() {
    return metadataMetaTitle;
  }

  public void setMetadataMetaTitle(String metadataMetaTitle) {
    this.metadataMetaTitle = metadataMetaTitle;
  }

  public String getMetadataOgTitle() {
    return metadataOgTitle;
  }

  public void setMetadataOgTitle(String metadataOgTitle) {
    this.metadataOgTitle = metadataOgTitle;
  }

  public List<Taxonomy> getProductStatus() {
    return productStatus;
  }

  public void setProductStatus(List<Taxonomy> productStatus) {
    this.productStatus = productStatus;
  }

  public String getAltitude() {
    return altitude;
  }

  public void setAltitude(String altitude) {
    this.altitude = altitude;
  }

  public String getMetadataMetaDescription() {
    return metadataMetaDescription;
  }

  public void setMetadataMetaDescription(String metadataMetaDescription) {
    this.metadataMetaDescription = metadataMetaDescription;
  }

  public String getVariety() {
    return variety;
  }

  public void setVariety(String variety) {
    this.variety = variety;
  }

  public List<Asset> getImage() {
    return image;
  }

  public void setImage(List<Asset> image) {
    this.image = image;
  }

  public String getMetadataTwitterSite() {
    return metadataTwitterSite;
  }

  public void setMetadataTwitterSite(String metadataTwitterSite) {
    this.metadataTwitterSite = metadataTwitterSite;
  }

  public String getUrlPattern() {
    return urlPattern;
  }

  public void setUrlPattern(String urlPattern) {
    this.urlPattern = urlPattern;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public List<Asset> getMetadataTwitterImage() {
    return metadataTwitterImage;
  }

  public void setMetadataTwitterImage(List<Asset> metadataTwitterImage) {
    this.metadataTwitterImage = metadataTwitterImage;
  }

  public String getMetadataTwitterCreator() {
    return metadataTwitterCreator;
  }

  public void setMetadataTwitterCreator(String metadataTwitterCreator) {
    this.metadataTwitterCreator = metadataTwitterCreator;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public List<Taxonomy> getSitemap() {
    return sitemap;
  }

  public void setSitemap(List<Taxonomy> sitemap) {
    this.sitemap = sitemap;
  }

  public String getMetadataTwitterTitle() {
    return metadataTwitterTitle;
  }

  public void setMetadataTwitterTitle(String metadataTwitterTitle) {
    this.metadataTwitterTitle = metadataTwitterTitle;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public List<Taxonomy> getProcessing() {
    return processing;
  }

  public void setProcessing(List<Taxonomy> processing) {
    this.processing = processing;
  }

  public String getMetadataTwitterDescription() {
    return metadataTwitterDescription;
  }

  public void setMetadataTwitterDescription(String metadataTwitterDescription) {
    this.metadataTwitterDescription = metadataTwitterDescription;
  }

  public List<Asset> getMetadataOgImage() {
    return metadataOgImage;
  }

  public void setMetadataOgImage(List<Asset> metadataOgImage) {
    this.metadataOgImage = metadataOgImage;
  }

  public String getLongDescription() {
    return longDescription;
  }

  public void setLongDescription(String longDescription) {
    this.longDescription = longDescription;
  }

  public String getFarm() {
    return farm;
  }

  public void setFarm(String farm) {
    this.farm = farm;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public System getSystem() {
    return system;
  }

  public void setSystem(System system) {
    this.system = system;
  }
}
