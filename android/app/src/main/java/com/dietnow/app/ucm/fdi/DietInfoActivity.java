package com.dietnow.app.ucm.fdi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dietnow.app.ucm.fdi.adapters.DietFollowedAdapter;
import com.dietnow.app.ucm.fdi.adapters.MyDietsAdapter;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.dietnow.app.ucm.fdi.model.diet.Diet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DietInfoActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference db;
    private StorageReference storageRef;
    private Button monday, tuesday, wednesday, thursday,friday,saturday,sunday,saveChanges,AbandonDiet;
    private CheckBox checkBox;
    private TextView aliment_id ,kcal_info,diet_description,diet_title;
    private EditText info_cantidad;

    private ArrayList<Aliment> alimentList;

    private com.dietnow.app.ucm.fdi.adapters.DietFollowedAdapter dietFollowedAdapter;
    private androidx.recyclerview.widget.RecyclerView RecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_info);
        getSupportActionBar().setTitle("Informacion de la dieta");


        // Atributos Firebase
        auth        = FirebaseAuth.getInstance();
        db          = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
        storageRef  = FirebaseStorage.getInstance().getReference(); // crear una instancia a la referencia del almacenamiento

        alimentList                = new ArrayList<Aliment> ();
        // Atributos de la vista
        monday       = findViewById(R.id.monday_button);
        tuesday      = findViewById(R.id.tuesday_button);
        wednesday    = findViewById(R.id.wednesday_button);
        thursday     = findViewById(R.id.thursday_button);
        friday       = findViewById(R.id.friday_button);
        saturday     = findViewById(R.id.saturday_button);
        sunday       = findViewById(R.id.sunday_button);
        saveChanges  = findViewById(R.id.save_changes);
        checkBox     = findViewById(R.id.id_checkBox);
        aliment_id   = findViewById(R.id.id_aliment);
        kcal_info    = findViewById(R.id.id_kcal);
        info_cantidad= findViewById(R.id.id_cantidad);
        AbandonDiet  = findViewById(R.id.desuscribirBtn);
        diet_description= findViewById(R.id.diet_name);
        diet_title = findViewById(R.id.viewDietDescription);


        RecyclerView            = findViewById(R.id.diet_followed_aliment);
        RecyclerView.setLayoutManager(new LinearLayoutManager(this));


        getAliments();


        // Acciones de los componentes
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.child("users").child(auth.getUid()).child("diet").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // VER LA FECHA ACTUAL
                        java.util.Date fecha = new Date();
                        //METER LOS ALIMENTOS A LA LISTA

                        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
                        String strDate = dateFormat.format(fecha);
                        System.out.println("FECHA ACTUAL:"+strDate);


                        db.child("diet_history").child(auth.getUid()).child(snapshot.getValue().toString()).setValue(strDate);
                        db.child("diet_history").child(auth.getUid()).child(snapshot.getValue().toString()).child(strDate).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                System.out.println("DIETA SEGUIDA POR EL USARIO"+snapshot);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        AbandonDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.child("users").child(auth.getUid()).child("diet").removeValue();
            }
        });

    }


    private void getAliments(){

        db.child("users").child(auth.getUid()).child("diet").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //diet_description.setText(db.child("diets").child(snapshot.getValue().toString()).get()
                //diet_title.setText(db.child("diets").child(snapshot.getValue().toString()).child("title").get().toString());
                if(snapshot.hasChildren()) {
                    db.child("diets").child(snapshot.getValue().toString()).child("aliments").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Aliment aliment = ds.getValue(Aliment.class);
                                alimentList.add(aliment);
                            }
                            dietFollowedAdapter = new DietFollowedAdapter(alimentList, DietInfoActivity.this);
                            RecyclerView.setAdapter(dietFollowedAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}
