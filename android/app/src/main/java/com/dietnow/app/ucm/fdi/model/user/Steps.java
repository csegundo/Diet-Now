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
public class Steps {

    private String id;
    private long user;
    private Integer weight;
    private Date date;

    //getters y setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
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
        return "Steps{" +
                "id=" + id +
                ", user=" + user +
                ", weight=" + weight +
                ", date=" + date +
                '}';
    }
}
