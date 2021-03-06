package com.dietnow.app.ucm.fdi.utils;

import android.util.Log;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DietNowTokens {
    public static String generateToken(String uid){
        try {
            // clave secreta conocida servidor-sevidor
            String secret = "NvW?ZsUKky#5z$^X2HkLWZ6H%jN5!h69";
            String sha256 = secret + new SimpleDateFormat("y-M-d").format(new Date()) + uid; // string que vamos a cifrar

            // crear el objeto MessageDigest para comporbar el cifrado
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            // pasar el string a cifrar al objeto MessageDigest
            messageDigest.update(sha256.getBytes());

            // generar los bytes de cifrado
            byte[] digest = messageDigest.digest();

            // Convertir el array de bytes al formato HexString
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < digest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & digest[i]));
            }

            Log.d("DIET NOW TOKEN: ", hexString.toString());
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
