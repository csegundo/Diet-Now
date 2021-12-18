package com.dietnow.app.ucm.fdi.DietNow;

public class GenerateResponse {
    public static String generateJSON(Boolean success, String message){
        return "{\"success\":" + success + ",\"message\":\"" + message + "\"}";
    }
}
