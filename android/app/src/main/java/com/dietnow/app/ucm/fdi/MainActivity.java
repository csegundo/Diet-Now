package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dietnow.app.ucm.fdi.model.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * LoginActivity - Establece el inicio de sesion del usuario en la aplicación
 */
public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;
    private Button register;

    private FirebaseAuth auth;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inicializar Google Firebase
        auth     = FirebaseAuth.getInstance();
        db       = FirebaseDatabase.getInstance().getReference();

        // Buscar los componentes de esta actividad por su ID
        register = findViewById(R.id.loginRegisterBtn);
        login    = findViewById(R.id.loginBtn);
        email    = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);

        // Acciones de los componentes
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(MainActivity.this, AdminPageActivity.class);
                // startActivity(intent);
                login(email.getText().toString(), password.getText().toString());
            }
        });
    }

    private void login(String email, String rawPassword){
        auth.signInWithEmailAndPassword(email, rawPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    updateUI(user);
                } else{
                    updateUI(null);
                }
            }
        });
    }

    // redirige a la home o muestra error de inicio de sesion
    private void updateUI(FirebaseUser user){
        String rol = User.UserRole.ADMIN.name(); // MUY TEMPORAL: VER PERSISTENCIA DE DATOS

        if(user != null){
            String uuid = user.getUid(), userName = user.getDisplayName();
            Toast.makeText(getApplicationContext(),
                    "¡" + getResources().getString(R.string.welcome) +
                            (userName != null ? ", " + userName : "") + "!",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, rol.equalsIgnoreCase("admin") ?
                    AdminPageActivity.class : UserPageActivity.class);
            startActivity(intent);
        } else{
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.login_failed),
                    Toast.LENGTH_LONG).show();
        }
    }

    // comprueba el estado de la autenticacion actual
    public Boolean checkLoginStatus() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // usuario logueado
        } else {
            // usuario sin loguear
        }
        return false;
    }
}