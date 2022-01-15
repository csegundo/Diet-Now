package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dietnow.app.ucm.fdi.adapters.AlimentsAdapter;
import com.dietnow.app.ucm.fdi.adapters.AllUsersAdapter;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.dietnow.app.ucm.fdi.model.diet.Diet;
import com.dietnow.app.ucm.fdi.model.user.User;
import com.dietnow.app.ucm.fdi.service.DietService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CreateDietActivity extends AppCompatActivity {

    private EditText title, description;
    private Button create;
    private ProgressBar progress;
    private String actualDiet;
    private FloatingActionButton addFood;
    private TextView alimentsLabel;

    private FirebaseAuth auth;
    private DatabaseReference db;

    //adapters
    private AlimentsAdapter AlimentsAdapter;
    private RecyclerView RecyclerView;
    private ArrayList<Aliment> alimentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_diet);

        // parametros intent (crear o editar dieta)
        actualDiet = null;
        if(getIntent().getExtras() != null){
            actualDiet = getIntent().getExtras().getString("did");
        }

        // Inicializar componentes de Firebase
        auth        = FirebaseAuth.getInstance();
        db          = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
        alimentList                = new ArrayList<Aliment> ();

        // Inicializar los componentes de la vista
        title       = findViewById(R.id.createDietTitle);
        create      = findViewById(R.id.createDietBtn);
        progress    = findViewById(R.id.createDietProgress);
        description = findViewById(R.id.createDietDescription);
        addFood     = findViewById(R.id.addFood);
        alimentsLabel = findViewById(R.id.dietAlimentsLabel);
        RecyclerView    = findViewById(R.id.AllAlimentsRecycler);

        RecyclerView.setLayoutManager(new LinearLayoutManager(this));



        if(actualDiet == null){
            addFood.setVisibility(View.GONE);
            alimentsLabel.setVisibility(View.GONE);
        }
        isEditOrCreateDiet(actualDiet);

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
                uploadDietToFirebase(toCreate, actualDiet);
            }
        });

        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateDietActivity.this);
                builder.setTitle(R.string.add_food_message)
                    .setItems(R.array.add_food_options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(CreateDietActivity.this,
                                    which == 0 ? CameraActivity.class : AddManualFood.class);
                            intent.putExtra("did",actualDiet);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.delete_alert_no_opt, null).show();
            }
        });
    }


    /**
     * Metodos/funciones auxiliares de ayuda
     */

    // Recibe el ID de la dieta o null en funcion de si está creando dieta o la esta editando
    private void isEditOrCreateDiet(String dietId){
        if(dietId != null){


            db.child("diets").child(dietId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Diet actual = snapshot.getValue(Diet.class);
                    title.setText(actual.getTitle());
                    description.setText(actual.getDescription());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            db.child("diets").child(dietId).child("aliments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                        /**
                         * En ds.getKey() tenemos el barcode del alimento
                         * En ds.getValue(Aliment.class) tenemos la info del alimento para ponerlo en la tabla
                         *
                         * NOTE: a cada fila de la tabla hay que meterle el barcode para poder eliminarlo
                         * */
                        //Log.d("Diet:" + actualDiet + "; Aliment", ds.toString());
                        Aliment aliment = ds.getValue(Aliment.class);
                        aliment.setId(ds.getKey());
                        if(aliment.isActive() ){
                            alimentList.add(aliment);
                        }
                    }
                    AlimentsAdapter = new AlimentsAdapter(alimentList,CreateDietActivity.this,actualDiet);
                    RecyclerView.setAdapter(AlimentsAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    // guardar la dieta en dieta y usuarios (dentro del callback para que si falla no haya que borrar la dieta)
    private void uploadDietToFirebase(Diet toCreate, String dietId){
        FirebaseUser currentUser = auth.getCurrentUser();
        String autoId = dietId != null ? dietId : db.child("diets").push().getKey();

        // guardar la dieta
        toCreate.setId(autoId);
        toCreate.setUser(currentUser.getUid());
        Log.d("NEW DIET", toCreate.toString());
        db.child("diets").child(autoId).setValue(toCreate);

        // guardar la dieta en el usuario
        // TODO firebase acepta arrays ???
        // User user = dataSnapshot.child("users").child(currentUser.getUid()).getValue(User.class);
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