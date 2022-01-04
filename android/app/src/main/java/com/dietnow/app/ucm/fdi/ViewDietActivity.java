package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.dietnow.app.ucm.fdi.model.diet.Diet;
import com.dietnow.app.ucm.fdi.model.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewDietActivity extends AppCompatActivity {

    private TextView name, description;
    private String actualDiet;

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

        initializeComponentsWithData(this.actualDiet);
    }

    /**
     * Metodos/funciones auxiliares
     */

    // Dado el ID de la dieta obtiene toda la info y asigna el valor a cada componente
    private void initializeComponentsWithData(String dietId){


        ///CON ESTO FUNCIONA TBN
        /*
        db.child("diets").child(dietId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });
        */



        //SIMPLEMENTE LE HE AÑADIDO EL FOR , DE LA MANERA QUE TENIAS NO SE PQ NO IBA LA VD
        db.child("diets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    if(postSnapshot.getKey().toString().equalsIgnoreCase(dietId))
                    Log.d("Info de la snapshot",postSnapshot.getValue().toString());
                }
                //System.out.println("Este es el id de la dieta :"+ dietId);
                //Log.d("DIETS DB", snapshot.child("diets").toString());
                //Log.d("DIETA --------------->", snapshot.child("diets").child(dietId).getValue().toString());
                //Log.d("DIETA --------------->", snapshot.child("diets").child(dietId).getValue().toString());
                /*
                // LA KEY ESTA BIEN PERO EL VALUE ES null Y NO LA DIETA WTF
                Diet diet = snapshot.child("diets").child(dietId).getValue(Diet.class);
                name.setText(diet.getTitle());
                description.setText(diet.getDescription());
                //
                */

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("GET DIET", "ViewDietActivity:onCancelled", error.toException());
            }
        });



    }
}