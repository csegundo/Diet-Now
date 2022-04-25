/*Codigo original que metia valores de manera dinamica
 * https://www.tutorialspoint.com/how-to-dynamically-update-a-listview-in-android
 * */

package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import com.dietnow.app.ucm.fdi.adapters.AllUsersAdapter;
import com.dietnow.app.ucm.fdi.model.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class AllUserActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private FirebaseAuth auth;
    private SearchView searchUser;
    private AllUsersAdapter AllUsersAdapter;
    private RecyclerView RecyclerView;
    private DatabaseReference bd;
    private ArrayList<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //----------------Variables--------------------
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);

        searchUser              = findViewById(R.id.searchUser);
        RecyclerView            = findViewById(R.id.AllUserRecycler);
        bd                      = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
        userList                = new ArrayList<User> ();

        RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //----------------Fin Variables----------------

        //----------------Lista de Usuarios------------------------
        getUser();
        //----------------Fin Lista de Usuarios--------------------

        //--------------------search--------------------------
        searchUser.setOnQueryTextListener(this);
        //------------------Fin search------------------------
    }

    private void getUser(){
        bd.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String email = ds.child("email").getValue().toString();
                    Boolean active = ds.child("active").getValue(Boolean.class);
                    String name = ds.child("name").getValue().toString();
                    User us = new User(email, name);
                    // Se añaden todos los usuarios pq se puede cambiar el status
                    userList.add(us);
                }
                AllUsersAdapter = new AllUsersAdapter(userList,AllUserActivity.this);
                RecyclerView.setAdapter(AllUsersAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        /*
        bd.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot ds : task.getResult().getChildren()) {
                    String email = ds.child("email").getValue().toString();
                    Boolean active = ds.child("active").getValue(Boolean.class);
                    String name = ds.child("name").getValue().toString();
                    User us = new User(email, name);
                    if(active == true) {
                        userList.add(us);
                    }
                }
                AllUsersAdapter = new AllUsersAdapter(userList,AllUserActivity.this);
                RecyclerView.setAdapter(AllUsersAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("OnFailureAllUser: ","");
                e.printStackTrace();
            }
        });
         */
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        AllUsersAdapter.filtrado(newText);
        return false;
    }
}










