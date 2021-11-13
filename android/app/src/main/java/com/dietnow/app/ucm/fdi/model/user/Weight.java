package com.dietnow.app.ucm.fdi.model.user;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@IgnoreExtraProperties
public class Weight {

    private String id;
    private String user;
    private Date date;
    private long weight;

    //getters y setters


    public long getSteps() {
        return weight;
    }

    public void setSteps(long weight) {
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("user", user);
        result.put("weight", weight);
        result.put("date", date);
        return result;
    }

    @Override
    public String toString() {
        return "Weight{" +
                "id=" + id +
                ", user=" + user +
                ", date=" + date +
                ", weight=" + weight +
                '}';
    }
}
