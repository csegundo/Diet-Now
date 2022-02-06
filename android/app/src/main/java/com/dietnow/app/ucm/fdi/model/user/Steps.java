package com.dietnow.app.ucm.fdi.model.user;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class Steps {
    @Exclude
    private String id;
    private long Steps;
    private String date;

    public Steps(String now, int steps) {
        date=now;
        Steps=steps;
    }

    //getters y setters


    public long getSteps() {
        return Steps;
    }

    public void setSteps(long steps) {
        Steps = steps;
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

        result.put(date, Steps);
        return result;
    }

    @Override
    public String toString() {
        return "Steps{" +
               date +"=" + Steps +
                '}';
    }
}
