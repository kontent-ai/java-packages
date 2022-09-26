package com.dancinggoat.models;

import java.lang.Double;
import java.lang.String;
import java.util.List;
import kontent.ai.delivery.Asset;
import kontent.ai.delivery.ContentItemMapping;
import kontent.ai.delivery.ElementMapping;
import kontent.ai.delivery.Option;
import kontent.ai.delivery.System;
import kontent.ai.delivery.Taxonomy;

/**
 * This code was generated by a <a href="https://github.com/kontent-ai/java-packages/tree/master/delivery-sdk-generators">delivery-sdk-generators tool</a>
 *
 * Changes to this file may cause incorrect behavior and will be lost if the code is regenerated.
 * For further modifications of the class, create a separate file and extend this class.
 */
@ContentItemMapping("brewer")
public class Brewer {
  @ElementMapping("product_name")
  String productName;

  @ElementMapping("price")
  Double price;

  @ElementMapping("image")
  List<Asset> image;

  @ElementMapping("product_status")
  List<Taxonomy> productStatus;

  @ElementMapping("manufacturer")
  String manufacturer;

  @ElementMapping("in_stock")
  List<Option> inStock;

  @ElementMapping("short_description")
  String shortDescription;

  @ElementMapping("long_description")
  String longDescription;

  System system;

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public List<Asset> getImage() {
    return image;
  }

  public void setImage(List<Asset> image) {
    this.image = image;
  }

  public List<Taxonomy> getProductStatus() {
    return productStatus;
  }

  public void setProductStatus(List<Taxonomy> productStatus) {
    this.productStatus = productStatus;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public List<Option> getInStock() {
    return inStock;
  }

  public void setInStock(List<Option> inStock) {
    this.inStock = inStock;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public String getLongDescription() {
    return longDescription;
  }

  public void setLongDescription(String longDescription) {
    this.longDescription = longDescription;
  }

  public System getSystem() {
    return system;
  }

  public void setSystem(System system) {
    this.system = system;
  }
}