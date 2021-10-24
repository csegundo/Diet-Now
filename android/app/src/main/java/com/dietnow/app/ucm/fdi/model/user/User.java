package com.dietnow.app.ucm.fdi.model.user;


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    private long id;
    private Integer age;
    private String email;
    private String name;
    private String lastname;

}
