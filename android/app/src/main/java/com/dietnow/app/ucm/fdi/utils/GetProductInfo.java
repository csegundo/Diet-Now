package com.dietnow.app.ucm.fdi.utils;

import android.content.Intent;
import android.util.Log;

import com.dietnow.app.ucm.fdi.AddManualFood;
import com.dietnow.app.ucm.fdi.CameraActivity;
import com.dietnow.app.ucm.fdi.CreateDietActivity;
import com.dietnow.app.ucm.fdi.MainActivity;
import com.dietnow.app.ucm.fdi.apis.OpenFoodFactsService;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.google.common.util.concurrent.AbstractListeningExecutorService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Contiene la llamada a la API de Open Food Facts para obtener la info de un alimento
 */
public class GetProductInfo {

    private static GetProductInfo instance;
    private DatabaseReference db;
    private Retrofit retrofit;

    public GetProductInfo(){
        db       = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
        retrofit = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    public static GetProductInfo getInstance(){
        if(instance == null){
            instance = new GetProductInfo();
        }
        return instance;
    }

    public void getInfo(String barcode, String dietId){
        if(!barcode.isEmpty()){
            OpenFoodFactsService api = retrofit.create(OpenFoodFactsService.class);
            Call<ProductResponse> request = api.getProductInfo(barcode);
            try{
                request.enqueue(new Callback<ProductResponse>() { // la ejecuta async (para sync: execute())
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        if(response.isSuccessful()){
                            ProductResponse apiResponse = response.body();
                            Log.d("SUCCES", apiResponse.toString());
                            Aliment aliment = new Aliment(apiResponse.getName(), apiResponse.getGrams(), apiResponse.getKcal());

                            // Guardar codigo de barras en el array de la dieta
                            db.child("diets").child(dietId).child("aliments").child(barcode).setValue(aliment);
                        } else {
                            Log.d("FAILED", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        Log.d("FAILED", "FAILED HTTP REQUEST");
                        t.printStackTrace();
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
