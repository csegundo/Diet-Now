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
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dietnow.app.ucm.fdi.adapters.AlimentViewOnlyAdapter;
import com.dietnow.app.ucm.fdi.adapters.AlimentsAdapter;
import com.dietnow.app.ucm.fdi.adapters.AllUsersAdapter;
import com.dietnow.app.ucm.fdi.adapters.DietDocsAdapter;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.dietnow.app.ucm.fdi.model.diet.Diet;
import com.dietnow.app.ucm.fdi.model.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewDietActivity extends AppCompatActivity {

    private TextView name, description, status, publishedBy, dietActionsLabel;
    private String actualDiet;
    private androidx.recyclerview.widget.RecyclerView RecyclerView, docTable;
    private Button edit, delete, publish, unpublish;
    private ImageButton follow;
    private AlimentViewOnlyAdapter alimentsAdapter;
    private DietDocsAdapter docsAdapter;
    private ArrayList<Aliment> alimentList;
    private ArrayList<Pair<String, String>> docList;
    private FirebaseAuth auth;
    private DatabaseReference db;
    private StorageReference storageRef;

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
        storageRef  = FirebaseStorage.getInstance().getReference(); // crear una instancia a la referencia del almacenamiento

        // Atributos de la vista
        name        = findViewById(R.id.viewDietName);
        description = findViewById(R.id.viewDietDescription);
        RecyclerView  = findViewById(R.id.dietAliment);
        docTable    = findViewById(R.id.dietDocs);
        edit        = findViewById(R.id.btnEditDiet);
        delete      = findViewById(R.id.btnDeleteDiet);
        publish     = findViewById(R.id.btnPublishDiet);
        unpublish   = findViewById(R.id.btnUnpublishDiet);
        status      = findViewById(R.id.statusDietLbl);
        publishedBy  = findViewById(R.id.publishedBy);
        dietActionsLabel= findViewById(R.id.dietActionsLabel);
        follow        = findViewById(R.id.followbtn);

        alimentList = new ArrayList<Aliment>();
        docList = new ArrayList<Pair<String, String>>();

        initializeComponentsWithData(this.actualDiet);
        RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        docTable.setLayoutManager(new LinearLayoutManager(this));
        getAliment();
        getDocuments();

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

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFollowDiet(actualDiet,true);
            }
        });
    }

    /**
     * Metodos/funciones auxiliares
     */

    private void toggleFollowDiet(String dietId, boolean publish){
        FirebaseUser user = auth.getCurrentUser();

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User actual = snapshot.child("users").child(user.getUid()).getValue(User.class);

                if(!actual.getDiet().isEmpty()){

                    if(actual.getDiet().equalsIgnoreCase(dietId)){
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.alertFollowingDiet), Toast.LENGTH_LONG).show();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewDietActivity.this);
                        builder.setTitle(R.string.alertFollowDiet)
                                .setItems(R.array.yes_no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0){
                                            followDiet(dietId);
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.delete_alert_no_opt, null).show();
                    }

                }else{
                    followDiet(dietId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void followDiet(String dietId){
        FirebaseUser user = auth.getCurrentUser();

        db.child("users").child(user.getUid()).child("diet").setValue(dietId).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d H:m:s");
                String created = dateFormat.format(new Date());
                //TODO PROVISIONAL ,VER CAPTURA
                db.child("diet_history").child(user.getUid()).child(dietId).setValue(created);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

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

    // TODO borrar la carpeta de los documentos de la dieta
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

    private void getDocuments(){
        storageRef.child("diets/" + this.actualDiet).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    String docUrl = item.toString().replace("gs://", "");
                    docUrl = "https://firebasestorage.googleapis.com/v0/b/" + item.getBucket();
                    docUrl += "/o/diets%2F" + actualDiet + "%2F" + item.getName(); // la ruta [/diets/<id>/<name>.pdf] encodea las '/' == '%2F'
                    docUrl += "?alt=media"; // añadir este parametro para que se visualice el PDF
                    // si se necesita permisos para leer (en nuestras reglas no es el caso) habria que poner otro parametro "token"
                    Pair<String, String> doc = new Pair<>(item.getName(), docUrl);
                    docList.add(doc);
                }
                docsAdapter = new DietDocsAdapter(docList, ViewDietActivity.this, actualDiet);
                docTable.setAdapter(docsAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {}
        });
    }

    // Dado el ID de la dieta obtiene toda la info y asigna el valor a cada componente
    private void initializeComponentsWithData(String dietId){
        // https://stackoverflow.com/questions/41579000/difference-between-addvalueeventlistener-and-addlistenerforsinglevalueevent
        db.child("diets").child(dietId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // How to return this value?
                FirebaseUser currentUser = auth.getCurrentUser();
                Diet actual = dataSnapshot.getValue(Diet.class);
                Log.d("INFO", actual.toString());

                name.setText(actual.getTitle());
                description.setText(actual.getDescription());

                Log.d("CURRENT USER: ", actual.getUser());
                Log.d("USER DE LA DIETA: ", currentUser.getUid());

                // Mostrar el NOMBRE de la persona que ha creado la dieta
                db.child("users").child(actual.getUser()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User u = snapshot.getValue(User.class);
                        publishedBy.setText(publishedBy.getText().toString() + " " + u.getName() + " " + u.getLastname());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

                // Ocultar botones de acciones si esta viendo la dieta de otro usuario
                db.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User u = snapshot.getValue(User.class);
                        if(!currentUser.getUid().equalsIgnoreCase(actual.getUser()) && !u.getRole().equalsIgnoreCase("ADMIN")){
                            dietActionsLabel.setVisibility(View.GONE);
                            publish.setVisibility(View.GONE);
                            unpublish.setVisibility(View.GONE);
                            delete.setVisibility(View.GONE);
                            edit.setVisibility(View.GONE);
                            follow.setVisibility(View.VISIBLE);
                            if(u.getDiet().equalsIgnoreCase(dietId)){
                                follow.setColorFilter(Color.YELLOW);
                            }
                        }else{
                            dietActionsLabel.setVisibility(View.VISIBLE);
                            publish.setVisibility(View.VISIBLE);
                            unpublish.setVisibility(View.VISIBLE);
                            delete.setVisibility(View.VISIBLE);
                            edit.setVisibility(View.VISIBLE);
                            follow.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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