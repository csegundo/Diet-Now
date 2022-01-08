package com.dietnow.app.ucm.fdi.apis;

import com.dietnow.app.ucm.fdi.utils.RetrofitResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Usando Retrofit llamamos a la API de buscar la informacion nutricional de un alimento
 * dado el numero del codigo de barras
 *
 * Reference: https://es.openfoodfacts.org/data
 */
public interface OpenFoodFactsService {
    @GET("api/v0/product/{barcode}.json")
    Call<Gson> getProductInfo(@Path("barcode") String barcode);
}
