package com.dietnow.app.ucm.fdi.service;

import com.dietnow.app.ucm.fdi.model.diet.Diet;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DietService {
    private static DietService instance;

    // Singleton DietService
    public static DietService getInstance(){
        if(instance == null){
            instance = new DietService();
        }
        return instance;
    }

    public Diet parseDiet(String name, String description, int visits, int likes, int dislikes,
                          double kcal, boolean active, boolean published){
        SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d H:m:s");
        String created = dateFormat.format(new Date());

        Diet newDiet = new Diet(name, description, visits, likes, dislikes, kcal, active, published, created);

        return newDiet;
    }
}
