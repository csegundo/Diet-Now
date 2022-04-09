package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.dietnow.app.ucm.fdi.adapters.PublishedDietAdapter;
import com.dietnow.app.ucm.fdi.model.diet.Diet;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DietHistory extends AppCompatActivity {
    private FirebaseAuth auth;
    private com.dietnow.app.ucm.fdi.adapters.PublishedDietAdapter historyDietAdapter;
    private androidx.recyclerview.widget.RecyclerView RecyclerView;
    private DatabaseReference bd;
    private ArrayList<Diet> dietList;
    private ArrayList<Diet> Dietas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_history);
        auth                    = FirebaseAuth.getInstance();
        RecyclerView            = findViewById(R.id.dietHistoryRecycler);
        bd                      = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
        dietList                = new ArrayList<Diet> ();
        Dietas                = (ArrayList<Diet>) getIntent().getExtras().getSerializable("Dietas");

        RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //----------------Lista de Dietas------------------------
        getDiet();
        //----------------Fin Lista de Dietas--------------------
    }
    private void getDiet(){

            bd.child("diet_history").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        for (Diet d : Dietas) {
                            if(d.getId().equals(ds.getKey()))dietList.add(d);
                        }
                    }
                    historyDietAdapter = new PublishedDietAdapter(dietList, DietHistory.this);
                    RecyclerView.setAdapter(historyDietAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }

}