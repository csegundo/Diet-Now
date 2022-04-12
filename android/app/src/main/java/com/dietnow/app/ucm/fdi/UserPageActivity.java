package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dietnow.app.ucm.fdi.adapters.DietFollowedAdapter;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.dietnow.app.ucm.fdi.model.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserPageActivity extends AppCompatActivity {

    private Button dieta, perfil, dietasCreadas, dietasPub;
    private FirebaseAuth auth;
    private DatabaseReference db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        dieta = findViewById(R.id.dietaBtn);

        auth   = FirebaseAuth.getInstance();

        db          = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();

        // Acciones de los componentes
        dieta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ver si sigue a alguna dieta
                db.child("users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);

                        if(user.getDiet() == null){
                            Toast.makeText(getApplicationContext(), "Todavía no sigues ninguna dieta, consulta la lista de dietas publicadas.", Toast.LENGTH_LONG).show();
                        }else{
                            Intent intent = new Intent(UserPageActivity.this, DietInfoActivity.class);
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
        perfil = findViewById(R.id.miPerfilBtn);


        // Acciones de los componentes
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(UserPageActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });
        dietasCreadas = findViewById(R.id.misDietasBtn);

        // Acciones de los componentes
        dietasCreadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(UserPageActivity.this, MyDietsActivity.class);
                startActivity(intent);
            }
        });
        dietasPub = findViewById(R.id.dietasPublicadasBtn);

        // Acciones de los componentes
        dietasPub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con Intent podemos "redirigir" al usuario a nueva actividad
                Intent intent = new Intent(UserPageActivity.this, AllPublishedDiets.class);
                startActivity(intent);
            }
        });
    }
}