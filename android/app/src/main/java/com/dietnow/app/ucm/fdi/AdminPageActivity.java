package com.dietnow.app.ucm.fdi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dietnow.app.ucm.fdi.apis.DietNowService;
import com.dietnow.app.ucm.fdi.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminPageActivity extends AppCompatActivity {

    private Button creardieta,perfil,dietasCreadas,dietasPub,crearUser,modUser;
    private Button logout;
    private Button test;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        // inicializar Google Firebase
        auth   = FirebaseAuth.getInstance();
        logout = findViewById(R.id.cerrarSesionTemp);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(AdminPageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        creardieta = findViewById(R.id.adminCrearDietaBtn);

        // Acciones de los componentes
        creardieta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(AdminPageActivity.this, AdminPageActivity.class);
                startActivity(intent);
            }
        });
        perfil = findViewById(R.id.adminPerfilBtn);

        // Acciones de los componentes
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(AdminPageActivity.this, AdminPageActivity.class);
                startActivity(intent);
            }
        });
        dietasCreadas = findViewById(R.id.adminMisDietasBtn);

        // Acciones de los componentes
        dietasCreadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(AdminPageActivity.this, AdminPageActivity.class);
                startActivity(intent);
            }
        });
        dietasPub = findViewById(R.id.adminDietasPublicadasBtn);

        // Acciones de los componentes
        dietasPub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(AdminPageActivity.this, RegisterAdminActivity.class);
                startActivity(intent);
            }
        });
        crearUser = findViewById(R.id.adminCrearUser);

        // Acciones de los componentes
        crearUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(AdminPageActivity.this, RegisterAdminActivity.class);
                startActivity(intent);
            }
        });
        modUser = findViewById(R.id.adminModUser);

        // Acciones de los componentes
        modUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(AdminPageActivity.this, AllUserActivity.class);
                startActivity(intent);
            }
        });

        test = findViewById(R.id.buttonTestuser);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = auth.getCurrentUser();
                HashMap<String, String> params = new HashMap<>();
                String hashCode = Utils.hashRequestString(currentUser.getUid());

                if(!hashCode.isEmpty()){
                    params.put("sender", currentUser.getUid());
                    params.put("email", "test@ucm.es");
                    params.put("password", "123456");
                    params.put("code", hashCode);

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://10.0.2.2:8080/") // https://developer.android.com/studio/run/emulator-networking#networkaddresses
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    DietNowService api = retrofit.create(DietNowService.class);
                    Call<Boolean> request = api.createFirebaseuser(params); // prepara la peticion
                    request.enqueue(new Callback<Boolean>() { // la ejecuta async (para sync: execute())
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            if(response.isSuccessful()){
                                Log.d("REQUEST RESPONSE", response.body().toString());
                                Boolean userCreated = response.body();
                            } else{
                                Log.d("REQUEST RESPONSE ERROR", "failed");
                            }
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            Log.d("RESPONSE FAILED", t.toString());
                            t.printStackTrace();
                        }
                    });
                } else{
                    // TOAST FALLO
                }

            }
        });
    }
}