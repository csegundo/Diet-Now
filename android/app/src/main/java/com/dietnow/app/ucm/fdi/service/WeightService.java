package com.dietnow.app.ucm.fdi.service;

import com.dietnow.app.ucm.fdi.model.user.Steps;
import com.dietnow.app.ucm.fdi.model.user.Weight;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WeightService {
    private static com.dietnow.app.ucm.fdi.service.WeightService instance;

    // Singleton StepsService
    public static com.dietnow.app.ucm.fdi.service.WeightService getInstance() {
        if (instance == null) {
            instance = new com.dietnow.app.ucm.fdi.service.WeightService();
        }
        return instance;
    }

    public Weight parseWeight(double weight){
        SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d");
        String created = dateFormat.format(new Date());
        Weight newWeight = new Weight(created,weight);

        return newWeight;
    }
}
