package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dietnow.app.ucm.fdi.model.diet.Diet;
import com.dietnow.app.ucm.fdi.model.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ViewDietActivity extends AppCompatActivity {

    private TextView name, description;
    private String actualDiet;
    private Button edit, delete;

    private FirebaseAuth auth;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_diet);
        getSupportActionBar().setTitle("Ver dieta");

        // parametros intent
        actualDiet  = getIntent().getExtras().getString("did");

        // Atributos Firebase
        auth        = FirebaseAuth.getInstance();
        db          = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();

        // Atributos de la vista
        name        = findViewById(R.id.viewDietName);
        description = findViewById(R.id.viewDietDescription);
        edit        = findViewById(R.id.btnEditDiet);
        delete      = findViewById(R.id.btnDeleteDiet);

        initializeComponentsWithData(this.actualDiet);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirigir a la vista de editar dieta o reutilizar (adaptandola) la de crear dieta
                Intent intent = new Intent(ViewDietActivity.this, CreateDietActivity.class);
                intent.putExtra("did", actualDiet);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteModalAndConfirm(actualDiet);
            }
        });
    }

    /**
     * Metodos/funciones auxiliares
     */

    private void showDeleteModalAndConfirm(String dietId){
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewDietActivity.this);
        builder.setTitle(R.string.delete_diet)
            .setMessage(R.string.delete_diet_message)
            .setPositiveButton(R.string.delete_alert_yes_opt, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.child("diets").child(dietId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent intent = new Intent(ViewDietActivity.this, MyDietsActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            })
            .setNegativeButton(R.string.delete_alert_no_opt, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
    }

    // Dado el ID de la dieta obtiene toda la info y asigna el valor a cada componente
    private void initializeComponentsWithData(String dietId){
        db.child("diets").child(dietId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Diet actual = snapshot.getValue(Diet.class);
                name.setText(actual.getTitle());
                description.setText(actual.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}