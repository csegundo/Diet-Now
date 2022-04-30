package com.dietnow.app.ucm.fdi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dietnow.app.ucm.fdi.model.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * LoginActivity - Establece el inicio de sesion del usuario en la aplicación
 */
public class MainActivity extends AppCompatActivity {

    public static String FIREBASE_DB_URL = "https://diet-now-f650d-default-rtdb.europe-west1.firebasedatabase.app/";

    private EditText email;
    private EditText password;
    private Button login;
    private Button register;
    private ProgressBar progress;

    private FirebaseAuth auth;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.label_login);

        // inicializar Google Firebase
        auth     = FirebaseAuth.getInstance();
        db       = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();

        // Buscar los componentes de esta actividad por su ID
        register = findViewById(R.id.loginRegisterBtn);
        login    = findViewById(R.id.loginBtn);
        email    = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        progress = findViewById(R.id.loginLoader);

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
                progress.setVisibility(View.VISIBLE);
                login(email.getText().toString(), password.getText().toString());
            }
        });
    }

    private void login(String email, String rawPassword){

        Query query = db.child("users").orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(snapshot.exists()){
                    User emailUser = snapshot.getChildren().iterator().next().getValue(User.class);

                    if(emailUser != null && emailUser.getActive()){
                        auth.signInWithEmailAndPassword(email, rawPassword).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser currentUser = auth.getCurrentUser();
                                    db.child("users").child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            User user = task.getResult().getValue(User.class);
                                            updateUI(user);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("OnFailureMainActivity: ","");
                                            e.printStackTrace();
                                        }
                                    });
                                } else{
                                    updateUI(null);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("OnFailureLogin: ","");
                                e.printStackTrace();
                            }
                        });
                    } else{
                        progress.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_failed_inactive), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


    }

    // redirige a la home o muestra error de inicio de sesion
    private void updateUI(User user){
        if(user != null){
            String userName = user.getName(), rol = user.getRole();
            Toast.makeText(getApplicationContext(),
                    "¡" + getResources().getString(R.string.welcome) +
                            (userName != null ? " " + userName : "") + "!",
                    Toast.LENGTH_SHORT).show();

            progress.setVisibility(View.INVISIBLE);

            Intent intent = new Intent(MainActivity.this, rol.equalsIgnoreCase("admin") ? AdminPageActivity.class : UserPageActivity.class);
            startActivity(intent);
            /**
             * finish() cierra la actividad pero hace el "esfuerzo" de quitarla de la memoria
             * onDestroy() la elimina completamente (dibujo => https://developer.android.com/reference/android/app/Activity)
             *
             * A efectos prácticos es lo mismo
             */
            finish();
        } else{
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.login_failed),
                    Toast.LENGTH_LONG).show();
        }
    }
}