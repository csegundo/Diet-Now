package com.dietnow.app.ucm.fdi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dietnow.app.ucm.fdi.model.diet.Diet;
import com.dietnow.app.ucm.fdi.model.user.User;
import com.dietnow.app.ucm.fdi.service.DietService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateDietActivity extends AppCompatActivity {

    private EditText title, description;
    private Button create;
    private ProgressBar progress;

    private FirebaseAuth auth;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_diet);

        // Inicializar componentes de Firebase
        auth        = FirebaseAuth.getInstance();
        db          = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();

        // Inicializar los componentes de la vista
        title       = findViewById(R.id.createDietTitle);
        create      = findViewById(R.id.createDietBtn);
        progress    = findViewById(R.id.createDietProgress);
        description = findViewById(R.id.createDietDescription);

        // Acciones de los componentes
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);

                Diet toCreate = DietService.getInstance().parseDiet(
                    title.getText().toString(),
                    description.getText().toString(),
                    0, 0, 0,
                    0.0, true, false
                );
                uploadDietToFirebase(toCreate);
            }
        });
    }

    /**
     * Metodos/funciones auxiliares de ayuda
     */

    // guardar la dieta en dieta y usuarios (dentro del callback para que si falla no haya que borrar la dieta)
    private void uploadDietToFirebase(Diet toCreate){
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser currentUser = auth.getCurrentUser();
                String autoId = db.child("diets").push().getKey();

                // guardar la dieta
                toCreate.setId(autoId);
                toCreate.setUser(currentUser.getUid());
                Log.d("NEW DIET", toCreate.toString());
                db.child("diets").child(autoId).setValue(toCreate);

                // guardar la dieta en el usuario
                // TODO firebase acepta arrays ???
                // User user = dataSnapshot.child("users").child(currentUser.getUid()).getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                updateUI(false);
            }
        });
    }

    private void updateUI(Boolean success){
        if(success){
            Toast.makeText(
                    getApplicationContext(),
                    getResources().getString(R.string.create_diet_success),
                    Toast.LENGTH_SHORT
            ).show();
        } else{
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.create_diet_error),
                    Toast.LENGTH_LONG).show();
        }

        progress.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(CreateDietActivity.this, UserPageActivity.class);
        startActivity(intent);
    }
}