package com.dietnow.app.ucm.fdi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * UserProfileActivity - Establece el perfil del usuario en la aplicación
 */
public class UserProfileActivity extends AppCompatActivity {


    private Button settings;
    private Image profileImage;
    private Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Buscar los componentes de esta actividad por su ID
        settings = findViewById(R.id.settings);

        // Acciones de los componentes
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(UserProfileActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        delete = findViewById(R.id.deleteProfile);

        // Acciones de los componentes
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                //Intent intent = new Intent(UserProfileActivity.this, RegisterActivity.class);
                //startActivity(intent);
            }
        });

        //falta hacer la lógica de la aplicacion




    }
}