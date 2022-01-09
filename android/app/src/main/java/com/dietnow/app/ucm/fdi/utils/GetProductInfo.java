package com.dietnow.app.ucm.fdi.utils;

import android.util.Log;

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
        retrofit = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        db          = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
    }

    public static GetProductInfo getInstance(){
        if(instance == null){
            instance = new GetProductInfo();
        }
        return instance;
    }

    public void getInfo(String barcode,String dietId){

        if(!barcode.isEmpty()){
            OpenFoodFactsService api = retrofit.create(OpenFoodFactsService.class);
            Call<ProductResponse> request = api.getProductInfo(barcode);
            try{

                request.enqueue(new Callback<ProductResponse>() { // la ejecuta async (para sync: execute())
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        if(response.isSuccessful()){
                            ProductResponse apiResponse = response.body();
                            Aliment aliment = new Aliment(apiResponse.getName(),apiResponse.getGrams(),apiResponse.getKcal());
                            Log.d("SUCCES", apiResponse.toString());
                            db.child("diets").child(dietId).child("aliment").setValue(aliment);

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
