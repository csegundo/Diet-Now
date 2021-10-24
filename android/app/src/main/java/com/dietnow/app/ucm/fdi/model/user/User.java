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
    private String password;
    private String gender;
    private Date start_date;
    private Double height;
    //poner su relaccion con la tabla
    private Double weight;
    private String role;
    //poner su relación con la tabla
    private Integer steps;
    private Boolean active;

    // quedam las relaciones

}
