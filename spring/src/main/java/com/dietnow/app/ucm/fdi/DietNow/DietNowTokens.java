package com.dietnow.app.ucm.fdi.DietNow;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DietNowTokens {
    private static final String DIET_NOW_TOKEN_SECRET = "NvW?ZsUKky#5z$^X2HkLWZ6H%jN5!h69";

    public static Boolean checkTokens(String uid, String hash){
        try {
            // clave secreta conocida servidor-sevidor
            String sha256 = DietNowTokens.DIET_NOW_TOKEN_SECRET + new SimpleDateFormat("y-M-d").format(new Date()) + uid; // string que vamos a cifrar

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

            System.out.println("DIET NOW TOKEN: " + hexString.toString());
            return hexString.toString().equalsIgnoreCase(hash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
