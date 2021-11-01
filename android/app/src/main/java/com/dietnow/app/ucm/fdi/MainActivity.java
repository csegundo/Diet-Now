package com.dietnow.app.ucm.fdi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * LoginActivity - Establece el inicio de sesion del usuario en la aplicación
 */
public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Buscar los componentes de esta actividad por su ID
        register = findViewById(R.id.loginRegisterBtn);

        // Acciones de los componentes
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        login = findViewById(R.id.loginBtn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(MainActivity.this, AdminPageActivity.class);
                startActivity(intent);
            }
        });
    }
}