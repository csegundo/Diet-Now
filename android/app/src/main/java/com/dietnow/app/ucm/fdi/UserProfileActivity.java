package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.dietnow.app.ucm.fdi.model.user.*;
import java.util.Map;

/**
 * UserProfileActivity - Establece el perfil del usuario en la aplicación
 */
public class UserProfileActivity extends AppCompatActivity {


    private Button settings;
    private Image profileImage;
    private Button delete;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_user_profile);

        // Buscar los componentes de esta actividad por su ID
        settings = findViewById(R.id.settings);

        //para el usuario logueado
        auth     = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://diet-now-f650d-default-rtdb.europe-west1.firebasedatabase.app/").getReference();


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
                updateUser();
            }
        });
    }

    private void updateUser() {
        FirebaseUser userAuth = auth.getCurrentUser();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child("users").child(userAuth.getUid()).getValue(User.class);
                user.setActive(false);
                Map<String, Object> userValues = user.toMap();
                mDatabase.child("users").child(userAuth.getUid()).updateChildren(userValues);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "UserPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(postListener);
    }

}