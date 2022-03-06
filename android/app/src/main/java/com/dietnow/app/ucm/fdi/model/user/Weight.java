package com.dietnow.app.ucm.fdi.model.user;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Weight {
    @Exclude
    private String id;
    private String date;
    private double weight;


    public Weight(String now, double weight) {
        date=now;
        this.weight=weight;
    }
    //getters y setters


    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put(date, weight);
        return result;
    }

    @Override
    public String toString() {
        return "Steps{" +
                date +"=" + weight +
                '}';
    }
}
