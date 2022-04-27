package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.dietnow.app.ucm.fdi.adapters.AllUsersAdapter;
import com.dietnow.app.ucm.fdi.adapters.MyDietsAdapter;
import com.dietnow.app.ucm.fdi.model.diet.Diet;
import com.dietnow.app.ucm.fdi.model.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyDietsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private FirebaseAuth auth;
    private SearchView searchDiet;
    private MyDietsAdapter MyDietsAdapter;
    private androidx.recyclerview.widget.RecyclerView RecyclerView;
    private DatabaseReference bd;
    private ArrayList<Diet> dietList;
    private FloatingActionButton newDietBtn;
    private String CurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //----------------Variables-------------------
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_diets);
        auth                    = FirebaseAuth.getInstance();
        searchDiet              = findViewById(R.id.searchMyDiet);
        RecyclerView            = findViewById(R.id.MyDietsRecycler);
        bd                      = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
        dietList                = new ArrayList<Diet> ();
        newDietBtn              = findViewById(R.id.newDietBtn);
        CurrentUser             = auth.getCurrentUser().getUid();
        RecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //----------------Fin Variables----------------

        //----------------Lista de Dietas------------------------
        getDiet();
        //----------------Fin Lista de Dietas--------------------

        //--------------------search--------------------------
        searchDiet.setOnQueryTextListener(this);
        //------------------Fin search------------------------

        //--------------------boton new--------------------------
        newDietBtn.setOnClickListener(new  View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyDietsActivity.this, CreateDietActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //------------------Fin boton new------------------------
    }
    private void getDiet(){
        bd.child("diets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dietList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String titulo = ds.child("title").getValue().toString();
                    Boolean active = ds.child("active").getValue(Boolean.class);
                    String descripcion = ds.child("description").getValue().toString();
                    String user = ds.child("user").getValue().toString();
                    Diet us = new Diet(descripcion,titulo);
                    us.setId(ds.child("id").getValue().toString());
                    if(active && user.equals(CurrentUser)) {
                        dietList.add(us);
                    }
                }
                MyDietsAdapter = new MyDietsAdapter(dietList,MyDietsActivity.this);
                RecyclerView.setAdapter(MyDietsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OnFailureMyDietsAc: ", error.toString());
            }
        });


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText != null){
            this.MyDietsAdapter.filtrado(newText);
        }
        return false;
    }

}