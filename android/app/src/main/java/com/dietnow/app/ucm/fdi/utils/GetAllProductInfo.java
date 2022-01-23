package com.dietnow.app.ucm.fdi.utils;

import android.os.StrictMode;
import android.util.Log;

import com.dietnow.app.ucm.fdi.MainActivity;
import com.dietnow.app.ucm.fdi.apis.OpenFoodFactsService;
import com.dietnow.app.ucm.fdi.model.diet.NutritionalInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Contiene la llamada a la API de Open Food Facts para obtener la info de un alimento
 */
public class GetAllProductInfo {

    private static GetAllProductInfo instance;
    private DatabaseReference db;
    private Retrofit retrofit;

    public GetAllProductInfo(){
        db       = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static GetAllProductInfo getInstance(){
        if(instance == null){
            instance = new GetAllProductInfo();
        }
        return instance;
    }

    public NutritionalInfo getInfo(String barcode){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        if(!barcode.isEmpty()){
            OpenFoodFactsService api = retrofit.create(OpenFoodFactsService.class);
            Call<ProductResponse> request = api.getAllProductInfo(barcode);
            try{

                Response<ProductResponse> apiResponse = request.execute();
                return new NutritionalInfo(apiResponse.body().getName(), apiResponse.body().getGrams(), apiResponse.body().getKcal(), apiResponse.body().getFat(),
                        apiResponse.body().getSaturatedFat(), apiResponse.body().getCarbs(), apiResponse.body().getSugar(), apiResponse.body().getProteins(), apiResponse.body().getSalt());

            } catch (Exception e){
                e.printStackTrace();
                Log.d("TAG", e.getMessage() );
            }
        }
        return null;
    }
}
