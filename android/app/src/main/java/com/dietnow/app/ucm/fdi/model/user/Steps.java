package com.dietnow.app.ucm.fdi.model.user;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@IgnoreExtraProperties
public class Steps {
    @Exclude
    private String id;
    private String user;
    private long Steps;
    private Date date;

    //getters y setters

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

    public long getWeight() {
        return Steps;
    }

    public void setWeight(long Steps) {
        this.Steps = Steps;
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
        result.put("Steps", Steps);
        result.put("date", date);
        return result;
    }

    @Override
    public String toString() {
        return "Steps{" +
                "id=" + id +
                ", user=" + user +
                ", Steps=" + Steps +
                ", date=" + date +
                '}';
    }
}
