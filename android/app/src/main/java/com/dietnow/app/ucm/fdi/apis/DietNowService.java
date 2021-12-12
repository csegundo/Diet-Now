package com.dietnow.app.ucm.fdi.apis;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Usando Retrofit llamamos a nuestra API en Spring para gestionar los usuarios con la
 * API Admin de Firebase Authentication
 *
 * https://square.github.io/retrofit/
 */
public interface DietNowService {
    @POST("dietnow/api/user/create")
    Call<String> createFirebaseuser(@Body HashMap<String, String> params);
}
