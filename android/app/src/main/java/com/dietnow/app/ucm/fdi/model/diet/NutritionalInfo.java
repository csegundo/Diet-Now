package com.dietnow.app.ucm.fdi.model.diet;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class NutritionalInfo {
    @Exclude
    private String id;
    private String name;
    private double grams, kcal, fat, saturatedFat, carbs, sugar, proteins, salt;

    public NutritionalInfo(String name, double grams, double kcal, double fat, double saturatedFat, double carbs, double sugar, double proteins, double salt) {
        this.name = name;
        this.grams = grams;
        this.kcal = kcal;
        this.fat = fat;
        this.saturatedFat = saturatedFat;
        this.carbs = carbs;
        this.sugar = sugar;
        this.proteins = proteins;
        this.salt = salt;

    }

    public NutritionalInfo(){};

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("grams", grams);
        result.put("kcal", kcal);
        result.put("fat", fat);
        result.put("saturatedFat", saturatedFat);
        result.put("carbs", carbs);
        result.put("sugar", sugar);
        result.put("proteins", proteins);
        result.put("salt", salt);

        return result;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
