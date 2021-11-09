package com.dietnow.app.ucm.fdi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class AdminPageActivity extends AppCompatActivity {

    private Button creardieta,perfil,dietasCreadas,dietasPub,crearUser,modUser,deleteUser;
    private Button logout;
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
                Intent intent = new Intent(AdminPageActivity.this, UserProfileActivity.class);
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
                Intent intent = new Intent(AdminPageActivity.this, AdminPageActivity.class);
                startActivity(intent);
            }
        });
        deleteUser = findViewById(R.id.adminDeleteUser);

        // Acciones de los componentes
        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(AdminPageActivity.this, AdminPageActivity.class);
                startActivity(intent);
            }
        });

    }
}