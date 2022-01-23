package com.dietnow.app.ucm.fdi.apis;

import com.dietnow.app.ucm.fdi.utils.ProductResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Usando Retrofit llamamos a la API de buscar la informacion nutricional de un alimento
 * dado el numero del codigo de barras
 *
 * Reference: https://es.openfoodfacts.org/data
 */
public interface OpenFoodFactsService {
    @GET("dietnow/api/product/")
    Call<ProductResponse> getProductInfo(@Query("barcode") String barcode);

    @GET("dietnow/api/nutritionalInfo/")
    Call<ProductResponse> getAllProductInfo(@Query("barcode") String barcode);
}
