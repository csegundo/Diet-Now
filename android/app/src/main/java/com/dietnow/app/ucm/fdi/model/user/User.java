package com.dietnow.app.ucm.fdi.model.user;


import java.sql.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class User {
    public enum UserGender{ MALE, FEMALE, NO_GENRE }
    public enum UserRole{ ADMIN, USER }

    @Id
    private long id;
    private Integer age;
    private String email;
    private String name;
    private String lastname;
    private String password;
    private String gender;
    private Date start_date;
    private Double height;

    private String role;
    private Boolean active;

    // quedam las relaciones

    //weight
    @OneToMany(mappedBy = "userEntity")
    private List<Weight> weights;

    //Steps
    @OneToMany(mappedBy = "userEntity")
    private List<Steps> steps;

    public User(){}

    public User(String email, String name, String lastname, String password, String gender, Double height, String role){
        this.email = email;
        this.name = name;
        this.lastname = lastname;
        this.password = password;
        this.gender = !gender.isEmpty() ? gender : UserGender.NO_GENRE.name();
        this.role = role.isEmpty() || role.equals(UserRole.USER.name())
                ? UserRole.USER.name() : UserRole.ADMIN.name();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
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
}
