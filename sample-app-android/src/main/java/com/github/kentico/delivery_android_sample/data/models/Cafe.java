package com.github.kentico.delivery_android_sample.data.models;


import kontent.ai.delivery.*;
import kontent.ai.delivery.System;

import java.util.List;

/**
 * This code was generated by a <a href="https://github.com/Kentico/cloud-generators-java">cloud-generators-java tool</a>
 *
 * Changes to this file may cause incorrect behavior and will be lost if the code is regenerated.
 * For further modifications of the class, create a separate file and extend this class.
 */
@ContentItemMapping("cafe")
public class Cafe {
    @ElementMapping("phone")
    String phone;

    @ElementMapping("city")
    String city;

    @ElementMapping("photo")
    List<Asset> photo;

    @ElementMapping("email")
    String email;

    @ElementMapping("country")
    String country;

    @ElementMapping("street")
    String street;

    @ElementMapping("state")
    String state;

    @ElementMapping("zip_code")
    String zipCode;

    @ElementMapping("sitemap")
    List<Taxonomy> sitemap;

    System system;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Asset> getPhoto() {
        return photo;
    }

    public void setPhoto(List<Asset> photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public List<Taxonomy> getSitemap() {
        return sitemap;
    }

    public void setSitemap(List<Taxonomy> sitemap) {
        this.sitemap = sitemap;
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }
}
