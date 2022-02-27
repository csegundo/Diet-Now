package com.dietnow.app.ucm.fdi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dietnow.app.ucm.fdi.apis.DietNowService;
import com.dietnow.app.ucm.fdi.utils.DietNowTokens;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminPageActivity extends AppCompatActivity {

    private Button creardieta,perfil,dietasCreadas,dietasPub,crearUser,modUser;
    private Button logout;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        // inicializar Google Firebase
        auth   = FirebaseAuth.getInstance();

        creardieta = findViewById(R.id.adminCrearDietaBtn);

        // Acciones de los componentes
        creardieta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(AdminPageActivity.this, CreateDietActivity.class);
                startActivity(intent);
            }
        });
        perfil = findViewById(R.id.adminPerfilBtn);

        // Acciones de los componentes
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(AdminPageActivity.this, AdminProfileActivity.class);
                startActivity(intent);
            }
        });
        dietasCreadas = findViewById(R.id.adminMisDietasBtn);

        // Acciones de los componentes
        dietasCreadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(AdminPageActivity.this, MyDietsActivity.class);
                startActivity(intent);
            }
        });
        dietasPub = findViewById(R.id.adminDietasPublicadasBtn);

        // Acciones de los componentes
        dietasPub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(AdminPageActivity.this, AllPublishedDiets.class);
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
    }
}