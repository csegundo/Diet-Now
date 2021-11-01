package com.dietnow.app.ucm.fdi.service;

import com.dietnow.app.ucm.fdi.model.user.User;

/**
 * Maneja la logica relacionada con los usuarios
 */
public class UserService {

    public Integer register(String email, String name, String lastname, String password, User.UserGender gender, Double height){
        User newUser = new User(email, name, lastname, this.encodePassword(password), gender.name(), height, User.UserRole.USER.name());
        return 0;
    }

    // Para el register: hashea la password que ha metido el usuario
    private String encodePassword(String rawPassword){
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    // Para el login: comprueba que la password de texto plano coincide con la cifrada en la bbdd
    private boolean passwordMatches(String rawPassword, String hashedPassword){
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}
