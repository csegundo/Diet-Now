package com.dietnow.app.ucm.fdi.utils;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductResponse {


    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("active")
    @Expose
    private boolean active;
    @SerializedName("grams")
    @Expose
    private double grams;
    @SerializedName("kcal")
    @Expose
    private double kcal;

    @SerializedName("fat")
    @Expose
    private double fat;

    @SerializedName("saturatedFat")
    @Expose
    private double saturatedFat;

    @SerializedName("carbs")
    @Expose
    private double carbs;

    @SerializedName("sugar")
    @Expose
    private double sugar;

    @SerializedName("proteins")
    @Expose
    private double proteins;

    @SerializedName("salt")
    @Expose
    private double salt;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getGrams() {
        return grams;
    }

    public void setGrams(double grams) {
        this.grams = grams;
    }

    public double getKcal() {
        return kcal;
    }

    public void setKcal(double kcal) {
        this.kcal = kcal;
    }

    public double getFat() { return fat; }

    public void setFat(double fat) { this.fat = fat; }

    public double getSaturatedFat() { return saturatedFat; }

    public void setSaturatedFat(double saturatedFat) { this.saturatedFat = saturatedFat; }

    public double getCarbs() { return carbs; }

    public void setCarbs(double carbs) { this.carbs = carbs; }

    public double getSugar() { return sugar; }

    public void setSugar(double sugar) { this.sugar = sugar; }

    public double getProteins() { return proteins; }

    public void setProteins(double proteins) { this.proteins = proteins; }

    public double getSalt() { return salt; }

    public void setSalt(double salt) { this.salt = salt; }

    @Override
    public String toString() {
        return "ProductResponse{" +
                "name='" + name + '\'' +
                ", active=" + active +
                ", grams=" + grams +
                ", kcal=" + kcal +
                '}';
    }
    public String toStringAll() {
        return "ProductResponse{" +
                "name='" + name + '\'' +
                ", active=" + active +
                ", grams=" + grams +
                ", kcal=" + kcal +
                ", fat=" + fat +
                ", saturatedFat=" + saturatedFat +
                ", carbs=" + carbs +
                ", sugar=" + sugar +
                ", proteins=" + proteins +
                ", salt=" + salt +
                '}';
    }
}
