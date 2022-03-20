package com.dietnow.app.ucm.fdi.model.user;

import com.google.firebase.database.*;
import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class User {
    public enum UserGender{ MALE, FEMALE, NO_GENRE }
    public enum UserRole{ ADMIN, USER }

    @Exclude
    private String id;

    private Integer age;
    private String email;
    private String name;
    private String lastname;
    private String password;
    private String gender;
    private String date;
    private Double height;

    //dieta seguida
    private String diet;

    private String role;
    private Boolean active;

    // quedam las relaciones

    public User(){}

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public User(String email, String name, String lastname, String password, String gender, Double height, String role, Integer age, String date, Boolean active){
        this.email = email;
        this.name = name;
        this.lastname = lastname;
        this.password = password;
        this.height = height;
        this.age = age;
        this.date = date;
        this.gender = !gender.isEmpty() ? gender : UserGender.NO_GENRE.name();
        this.role = role.isEmpty() || role.equals(UserRole.USER.name())
                ? UserRole.USER.name() : UserRole.ADMIN.name();
        this.active=active;
        this.diet = "";
    }

    public User(String email, String name, String lastname, String password, String gender, Double height, String role,Boolean active){
        this.email = email;
        this.name = name;
        this.lastname = lastname;
        this.password = password;
        this.height = height;
        this.gender = !gender.isEmpty() ? gender : UserGender.NO_GENRE.name();
        this.role = role.isEmpty() || role.equals(UserRole.USER.name())
                ? UserRole.USER.name() : UserRole.ADMIN.name();
        this.active = active;

        this.diet = "";
    }

    public User(String email, Boolean active){
        this.email = email;
        this.active = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", diet='" + diet + '\'' +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", password='" + password + '\'' +
                ", gender='" + gender + '\'' +
                ", start_date=" + date +
                ", height=" + height +
                ", role='" + role + '\'' +
                ", active=" + active +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("active", false);
        result.put("age", age);
        result.put("email", email);
        result.put("gender", gender);
        result.put("lastname", lastname);
        result.put("name", name);
        result.put("password",password);
        result.put("rol", role);
        result.put("diet",diet);

        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStart_date() {
        return date;
    }

    public void setStart_date(String start_date) {
        this.date = start_date;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }


    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

}
