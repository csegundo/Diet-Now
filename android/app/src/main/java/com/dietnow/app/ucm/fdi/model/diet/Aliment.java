package com.dietnow.app.ucm.fdi.model.diet;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Aliment {
    @Exclude
    private String id;
    private String name;
    private boolean active;
    private double grams, kcal;


    @Exclude
    private double grams_consumed;

    @Override
    public String toString() {
        return "Aliment{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", grams=" + grams +
                ", kcal=" + kcal +
                ", grams_consumed=" + grams_consumed +
                '}';
    }

    public Aliment(String name, double grams, double kcal) {
        this.name = name;
        this.grams = grams;
        this.kcal = kcal;
        active = true;
    }

    public Aliment(){};

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("active", active);
        result.put("grams", grams);
        result.put("kcal", kcal);

        return result;
    }

    public double getGrams_consumed() {
        return grams_consumed;
    }

    public void setGrams_consumed(double grams_consumed) {
        this.grams_consumed = grams_consumed;
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
}
