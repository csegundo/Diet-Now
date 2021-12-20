package com.dietnow.app.ucm.fdi.model.diet;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * "user" es el UID del usuario que ha creado la dieta
 */
@IgnoreExtraProperties
public class Diet {
    @Exclude
    private String id;
    private String description, title, date, user;
    private int visits, likes, dislikes;
    private double kcal;
    private boolean active, published;

    public Diet(){}

    public Diet(String descripcion, String titulo) { //para mis dietas creadas
        this.description = descripcion;
        this.title = titulo;
    }

    public Diet(String name, String description, int visits, int likes, int dislikes,
                double kcal, boolean active, boolean published, String created){
        this.title = name;
        this.description = description;
        this.visits = visits;
        this.likes = likes;
        this.dislikes = dislikes;
        this.kcal = kcal;
        this.active = active;
        this.published = published;
        this.date = created;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("descripcion", description);
        result.put("active", active);
        result.put("visitas", visits);
        result.put("like", likes);
        result.put("dislike", dislikes);
        result.put("titulo", title);
        result.put("publicado", published);
        result.put("kcal", kcal);

        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String descripcion) {
        this.description = descripcion;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String titulo) {
        this.title = titulo;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public double getKcal() {
        return kcal;
    }

    public void setKcal(double kcal) {
        this.kcal = kcal;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Diet{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", user='" + user + '\'' +
                ", visits=" + visits +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", kcal=" + kcal +
                ", active=" + active +
                ", published=" + published +
                '}';
    }
}
