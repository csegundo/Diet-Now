package com.dietnow.app.ucm.fdi.service;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dietnow.app.ucm.fdi.model.user.User;
import com.dietnow.app.ucm.fdi.utils.BCrypt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Maneja la logica relacionada con los usuarios
 */
public class UserService {

    private static UserService instance;

    // return user id
    public User register(String email, String name, String lastname, String password, User.UserGender gender, Double height,Integer age){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("Y-M-d H:m:s");
        try{
            date = dateFormat.parse(dateFormat.format(date));
        }catch (Exception e ){
            e.printStackTrace();
        }

        User newUser = new User(email, name, lastname, this.encodePassword(password), gender.name(), height, User.UserRole.USER.name(),age,
                date!=null ? date:new Date());
        return newUser;
    }

    // Para el register: hashea la password que ha metido el usuario
    private String encodePassword(String rawPassword){
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    // Para el login: comprueba que la password de texto plano coincide con la cifrada en la bbdd
    private boolean passwordMatches(String rawPassword, String hashedPassword){
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }

    // Singleton UserService
    public static UserService getInstance(){
        if(instance == null){
            instance = new UserService();
        }
        return instance;
    }
}
