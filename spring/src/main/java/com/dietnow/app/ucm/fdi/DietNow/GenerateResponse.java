package com.dietnow.app.ucm.fdi.DietNow;

/**
 * Genera un String JSON para devolverlo a Retrofit
 */
public class GenerateResponse {
    public static String generateJSON(Boolean success, String message){
        return "{\"success\":" + success + ",\"message\":\"" + message + "\"}";
    }
}
