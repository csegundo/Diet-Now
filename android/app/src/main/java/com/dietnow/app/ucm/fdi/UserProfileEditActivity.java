package com.dietnow.app.ucm.fdi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * Desde la vista ADMIN de todos los usuarios, en la tabla hay acciones sobre los usuarios como
 * eliminar o editar. La accion de editar se realiza sobre esta pagina
 *
 * Tambien sirve para que un usuario edite su propio perfil
 */
public class UserProfileEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);
    }
}