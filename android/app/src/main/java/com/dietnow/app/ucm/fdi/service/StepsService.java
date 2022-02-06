package com.dietnow.app.ucm.fdi.service;

import com.dietnow.app.ucm.fdi.model.user.Steps;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StepsService {

    private static com.dietnow.app.ucm.fdi.service.StepsService instance;

    // Singleton StepsService
    public static com.dietnow.app.ucm.fdi.service.StepsService getInstance() {
        if (instance == null) {
            instance = new com.dietnow.app.ucm.fdi.service.StepsService();
        }
        return instance;
    }

    public Steps parseSteps(int steps){
        SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d");
        String created = dateFormat.format(new Date());
    Steps newSteps = new Steps(created,steps);

        return newSteps;
    }
}
