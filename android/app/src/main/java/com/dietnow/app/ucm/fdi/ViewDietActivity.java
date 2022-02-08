package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dietnow.app.ucm.fdi.adapters.AlimentViewOnlyAdapter;
import com.dietnow.app.ucm.fdi.adapters.AlimentsAdapter;
import com.dietnow.app.ucm.fdi.adapters.AllUsersAdapter;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
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

import java.util.ArrayList;
import java.util.List;

public class ViewDietActivity extends AppCompatActivity {

    private TextView name, description, status;
    private String actualDiet;
    private androidx.recyclerview.widget.RecyclerView RecyclerView;
    private Button edit, delete, publish, unpublish;
    private AlimentViewOnlyAdapter alimentsAdapter;
    private ArrayList<Aliment> alimentList;
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
        RecyclerView  = findViewById(R.id.dietAliment);
        edit        = findViewById(R.id.btnEditDiet);
        delete      = findViewById(R.id.btnDeleteDiet);
        publish     = findViewById(R.id.btnPublishDiet);
        unpublish   = findViewById(R.id.btnUnpublishDiet);
        status      = findViewById(R.id.statusDietLbl);
        alimentList = new ArrayList<Aliment>();

        initializeComponentsWithData(this.actualDiet);
        RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        getAliment();

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

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDietPublication(actualDiet, true);
            }
        });

        unpublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDietPublication(actualDiet, false);
            }
        });
    }

    /**
     * Metodos/funciones auxiliares
     */
    private void toggleDietPublication(String dietId, boolean publish){
        db.child("diets").child(dietId).child("published").setValue(publish);
        if(publish){
            status.setText(R.string.published_diet);
            status.setTextColor(Color.parseColor("#4CAF50"));
        } else{
            status.setText(R.string.unpublished_diet);
            status.setTextColor(Color.parseColor("#DC1414"));
        }
    }

    private void showDeleteModalAndConfirm(String dietId){
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewDietActivity.this);
        builder.setTitle(R.string.delete_diet)
            .setMessage(R.string.delete_diet_message)
            .setPositiveButton(R.string.delete_alert_yes_opt, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //System.out.println("BORRA LA DIETA CON ID: "+ dietId);

                    db.child("diets").child(dietId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //System.out.println("BORRA LA DIETA dentro del oNSUCCES");
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

    private void getAliment(){
        db.child("diets").child(actualDiet).child("aliments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Aliment aliment = ds.getValue(Aliment.class);
                    aliment.setId(ds.getKey());
                    if(aliment.isActive() ){
                        alimentList.add(aliment);
                    }
                }
                alimentsAdapter = new AlimentViewOnlyAdapter(alimentList,ViewDietActivity.this,actualDiet);
                RecyclerView.setAdapter(alimentsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Dado el ID de la dieta obtiene toda la info y asigna el valor a cada componente
    private void initializeComponentsWithData(String dietId){

        //SI LO HACEMOS CON EL ADDVALUEEVENTLISTENER AL BORRARLO SALTA A ESTA FUNCION Y CASCA, YA QUE TIENE LA REFERENCIA A LA DIETA ACTUAL
        /*
        db.child("diets").child(dietId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Diet actual = snapshot.getValue(Diet.class);
                System.out.println("INTENTA COGER EL VALOR DE LA DIETA");
                name.setText(actual.getTitle());
                description.setText(actual.getDescription());

                if(actual.isPublished()){
                    status.setText(R.string.published_diet);
                    status.setTextColor(Color.parseColor("#4CAF50"));
                } else{
                    status.setText(R.string.unpublished_diet);
                    status.setTextColor(Color.parseColor("#DC1414"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
         */

        /*
        db.child("diets").child(dietId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("FIREBASE EN GET DIET", "Error getting data", task.getException());
                }
                else {
                    Log.d("FIREBASE EN GET DIET", String.valueOf(task.getResult().getValue()));
                    Diet actual = task.getClass(Diet.class);
                    Log.d("VALOR DE DIETA ACTUAL", String.valueOf(actual));

                    name.setText(actual.getTitle());
                    description.setText(actual.getDescription());

                    if(actual.isPublished()){
                        status.setText(R.string.published_diet);
                        status.setTextColor(Color.parseColor("#4CAF50"));
                    } else{
                        status.setText(R.string.unpublished_diet);
                        status.setTextColor(Color.parseColor("#DC1414"));
                    }



                }
            }
        });
        */
        db.child("diets").child(dietId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // How to return this value?
                Diet actual = dataSnapshot.getValue(Diet.class);
                Log.d("INFO", actual.toString());

                name.setText(actual.getTitle());
                description.setText(actual.getDescription());

                if(actual.isPublished()){
                    status.setText(R.string.published_diet);
                    status.setTextColor(Color.parseColor("#4CAF50"));
                } else{
                    status.setText(R.string.unpublished_diet);
                    status.setTextColor(Color.parseColor("#DC1414"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
}