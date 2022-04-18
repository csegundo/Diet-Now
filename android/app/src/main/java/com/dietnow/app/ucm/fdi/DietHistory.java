package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dietnow.app.ucm.fdi.adapters.PublishedDietAdapter;
import com.dietnow.app.ucm.fdi.model.diet.Diet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DietHistory extends AppCompatActivity {
    private FirebaseAuth auth;
    private com.dietnow.app.ucm.fdi.adapters.PublishedDietAdapter historyDietAdapter;
    private androidx.recyclerview.widget.RecyclerView RecyclerView;
    private DatabaseReference bd;
    private ArrayList<Diet> dietList;
    private ArrayList<Diet> Dietas;
    private TextView titulo,desc,likes,visit;
    private Button ver;
    private String id,dietId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_history);
        auth                    = FirebaseAuth.getInstance();
        RecyclerView            = findViewById(R.id.dietHistoryRecycler);
        bd                      = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
        dietList                = new ArrayList<Diet> ();
        Dietas                  = (ArrayList<Diet>) getIntent().getExtras().getSerializable("Dietas");
        titulo                  =findViewById(R.id.hDietTitulo);
        desc                    =findViewById(R.id.hDietDesc);
        likes                   =findViewById(R.id.hNlikesDiet);
        visit                   =findViewById(R.id.hNVisitDiet);
        ver                     =findViewById(R.id.hDietShowBtn);
        dietId                  = "";

        RecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //----------------Dieta Actual--------------------------
        getDietInfo();
        //----------------Fin Dieta Actual-----------------------

        //----------------Lista de Dietas------------------------
        getDiet();
        //----------------Fin Lista de Dietas--------------------
    }

    private void getDietInfo(){
        bd.child("users").child(auth.getUid()).child("diet").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                bd.child("diets").child(snapshot.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Diet diet = snapshot.getValue(Diet.class);
                        dietId = diet.getId();
                        Log.d("Dieta actual :",diet.toString());
                        titulo.setText(diet.getTitle());
                        desc.setText(diet.getDescription());
                        id = diet.getId();
                        HashMap<String, Boolean> visita = diet.getVisits();
                        HashMap<String, Boolean> rating = diet.getRating();
                        visit.setText(String.valueOf(visita.size()));
                        Integer counter =0;
                        if(rating != null) {
                            for (Boolean type : rating.values()) {
                                counter += type ? 1 : 0;
                            }
                        }
                        likes.setText(String.valueOf(counter));
                        ver.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(DietHistory.this, ViewDietActivity.class);
                                intent.putExtra("did", id);
                                startActivity(intent);
                            }
                        });
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

    private void getDiet(){

        // ver que dieta es la actual para no meterla en la lista de dietas anteriores

            bd.child("users").child(auth.getUid()).child("diet").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (Diet d : Dietas) {
                        if(!d.getId().equals(snapshot.getValue(String.class)))dietList.add(d);
                    }

                    historyDietAdapter = new PublishedDietAdapter(dietList,dietId, DietHistory.this);
                    RecyclerView.setAdapter(historyDietAdapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }

}