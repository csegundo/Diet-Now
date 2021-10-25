package com.dietnow.app.ucm.fdi.model.user;


import java.sql.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

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
    private String role;
    private Boolean active;

    // quedam las relaciones

    //weight
    @OneToMany(mappedBy = "userEntity")
    private List<Weight> weights;

    //Steps
    @OneToMany(mappedBy = "userEntity")
    private List<Steps> steps;
}
