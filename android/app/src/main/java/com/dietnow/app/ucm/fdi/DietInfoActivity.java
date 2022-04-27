package com.dietnow.app.ucm.fdi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dietnow.app.ucm.fdi.adapters.DietFollowedAdapter;
import com.dietnow.app.ucm.fdi.adapters.MyDietsAdapter;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.dietnow.app.ucm.fdi.model.diet.Diet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DietInfoActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference db;
    private StorageReference storageRef;
    private Button monday, tuesday, wednesday, thursday, friday, saturday, sunday, comment, AbandonDiet, like, dislike,guardar;
    private CheckBox checkBox;
    private TextView aliment_id, kcal_info, diet_description, diet_title;
    private EditText info_cantidad;
    private String dietId, dayName; // ID de la dieta que está siguiendo
    private HashMap<String, Aliment> original_aliments;

    private ArrayList<Aliment> alimentList;
    private ArrayList<Button> listaBotones;

    private com.dietnow.app.ucm.fdi.adapters.DietFollowedAdapter dietFollowedAdapter;
    private androidx.recyclerview.widget.RecyclerView RecyclerView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_info);
        getSupportActionBar().setTitle("Informacion de la dieta");

        // Atributos Firebase
        auth        = FirebaseAuth.getInstance();
        db          = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
        storageRef  = FirebaseStorage.getInstance().getReference(); // crear una instancia a la referencia del almacenamiento

        // Atributos de la vista
        alimentList  = new ArrayList<Aliment> ();
        listaBotones  = new ArrayList<Button>();

        like         = findViewById(R.id.actualDietLike);
        dislike      = findViewById(R.id.actualDietDislike);
        monday       = findViewById(R.id.monday_button);
        tuesday      = findViewById(R.id.tuesday_button);
        wednesday    = findViewById(R.id.wednesday_button);
        thursday     = findViewById(R.id.thursday_button);
        friday       = findViewById(R.id.friday_button);
        saturday     = findViewById(R.id.saturday_button);
        guardar      = findViewById(R.id.GuardarAlimentoDietaBtn);
        sunday       = findViewById(R.id.sunday_button);
        comment      = findViewById(R.id.comment_diet);
        checkBox     = findViewById(R.id.id_checkBox);
        aliment_id   = findViewById(R.id.id_aliment);
        kcal_info    = findViewById(R.id.id_kcal);
        info_cantidad= findViewById(R.id.id_cantidad);
        AbandonDiet  = findViewById(R.id.desuscribirBtn);
        diet_title   = findViewById(R.id.diet_name);
        diet_description = findViewById(R.id.viewDietDescription);
        RecyclerView  = findViewById(R.id.diet_followed_aliment);
        RecyclerView.setLayoutManager(new LinearLayoutManager(this));

        original_aliments = new HashMap<String, Aliment>();

        listaBotones.add(monday);
        listaBotones.add(tuesday);
        listaBotones.add(wednesday);
        listaBotones.add(thursday);
        listaBotones.add(friday);
        listaBotones.add(saturday);
        listaBotones.add(sunday);

        //listener();
        getAliments();
        getDietInfo();



        AbandonDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.child("users").child(auth.getUid()).child("diet").removeValue();
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDietRating(true);
            }
        });
        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDietRating(false);
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DietInfoActivity.this, DietComments.class);
                intent.putExtra("did", dietId);
                startActivity(intent);
            }
        });


        LocalDateTime  currentDate = LocalDateTime.now();
        DayOfWeek day = currentDate.getDayOfWeek();
        String dayName = day.name().toLowerCase(Locale.ROOT);
        this.dayName = dayName;

        colorButtons();

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dietFollowedAdapter.guardar();
                Toast.makeText(getApplicationContext(), getResources().getString(com.dietnow.app.ucm.fdi.R.string.aliements_updated_succesfully), Toast.LENGTH_SHORT).show();
            }
        });

        monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorButtons();
               if(!dayName.equalsIgnoreCase("monday")){
                   monday.setBackgroundColor(Color.DKGRAY);
                   guardar.setVisibility(View.GONE);
                   getDietFromOtherDay(getDate(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")).split(" ")[0]);
               }else{
                   guardar.setVisibility(View.VISIBLE);
                   getAliments();

               }
            }
        });
        tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorButtons();
                if(!dayName.equalsIgnoreCase("tuesday")){
                    tuesday.setBackgroundColor(Color.DKGRAY);
                    guardar.setVisibility(View.GONE);
                    getDietFromOtherDay(getDate(2).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")).split(" ")[0]);
                }else{
                    guardar.setVisibility(View.VISIBLE);
                    getAliments();
                }
            }
        });
        wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorButtons();
                if(!dayName.equalsIgnoreCase("wednesday")){
                    wednesday.setBackgroundColor(Color.DKGRAY);
                    guardar.setVisibility(View.GONE);
                    getDietFromOtherDay(getDate(3).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")).split(" ")[0]);
                }else{
                    guardar.setVisibility(View.VISIBLE);
                    getAliments();
                }
            }
        });
        thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorButtons();
                if(!dayName.equalsIgnoreCase("thursday")){
                    thursday.setBackgroundColor(Color.DKGRAY);
                    guardar.setVisibility(View.GONE);
                    getDietFromOtherDay(getDate(4).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")).split(" ")[0]);
                }else{
                    guardar.setVisibility(View.VISIBLE);
                    getAliments();
                }
            }
        });
        friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorButtons();
                if(!dayName.equalsIgnoreCase("friday")){
                    friday.setBackgroundColor(Color.DKGRAY);
                    guardar.setVisibility(View.GONE);
                    getDietFromOtherDay(getDate(5).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")).split(" ")[0]);
                }else{
                    guardar.setVisibility(View.VISIBLE);
                    getAliments();
                }
            }
        });
        saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorButtons();
                if(!dayName.equalsIgnoreCase("saturday")){
                    saturday.setBackgroundColor(Color.DKGRAY);
                    guardar.setVisibility(View.GONE);
                    getDietFromOtherDay(getDate(6).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")).split(" ")[0]);
                }else{
                    guardar.setVisibility(View.VISIBLE);
                    getAliments();
                }

            }
        });
        sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorButtons();
                if(!dayName.equalsIgnoreCase("sunday")){
                    sunday.setBackgroundColor(Color.DKGRAY);
                    guardar.setVisibility(View.GONE);
                    getDietFromOtherDay(getDate(7).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")).split(" ")[0]);
                }else{
                    guardar.setVisibility(View.VISIBLE);
                    getAliments();
                }
            }
        });
    }

    private void colorButtons(){
        for(Button b : listaBotones){
            b.setBackgroundColor(Color.MAGENTA);
        }
        // coloreo el del dia actual
        switch (dayName){
            case "monday":
                monday.setBackgroundColor(Color.LTGRAY);
                break;
            case "tuesday":
                tuesday.setBackgroundColor(Color.LTGRAY);
                break;
            case "wednesday":
                wednesday.setBackgroundColor(Color.LTGRAY);
                break;
            case "thursday":
                thursday.setBackgroundColor(Color.LTGRAY);
                break;
            case "friday":
                friday.setBackgroundColor(Color.LTGRAY);
                break;
            case "saturday":
                saturday.setBackgroundColor(Color.LTGRAY);
                break;
            case "sunday":
                sunday.setBackgroundColor(Color.LTGRAY);
                break;

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDateTime getDate (int wanted_day_position){
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime wanted_date;

        int current_day_position = currentDate.getDayOfWeek().getValue();


        if(current_day_position > wanted_day_position){
            wanted_date = currentDate.minusDays(current_day_position - wanted_day_position);
        }else{
            wanted_date = currentDate.plusDays(wanted_day_position - current_day_position);

        }

        return wanted_date;
    }

    private void listener(){
        db.child("diet_history").child(auth.getUid()).child(dietId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getAliments();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getDietFromOtherDay(String wanted_day){
        db.child("diet_history").child(auth.getUid()).child(dietId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                // para no alterar el otro mapa original que tiene los alimentos por defecto de la dieta
                HashMap<String, Aliment> local_copy = new HashMap<String, Aliment>();
                for(Map.Entry<String, Aliment> entry : original_aliments.entrySet()){
                    Aliment mapAliment = entry.getValue();
                    Aliment newAliment = new Aliment(mapAliment.getName(), mapAliment.getGrams(), mapAliment.getKcal());
                    newAliment.setId(mapAliment.getId());
                    local_copy.put(entry.getKey(), newAliment);
                }

                for (DataSnapshot ds : task.getResult().getChildren()){
                    if(wanted_day.equalsIgnoreCase(ds.getKey().split(" ")[0].trim())){
                        for (DataSnapshot ds2 : ds.getChildren()){
                            System.out.println("ACTUALIZO EL ALIMENTO " + ds2.getKey().trim() + " CON GRAMOS " + ds2.getValue(double.class));
                            Aliment aliment = local_copy.get(ds2.getKey().trim());
                            System.out.println("ORIGINAL " + original_aliments.get(ds2.getKey().trim()).toString());
                            System.out.println("COPY " + local_copy.get(ds2.getKey().trim()).toString());

                            Double d = aliment.getGrams_consumed() + ds2.getValue(double.class);
                            aliment.setGrams_consumed(d);
                            local_copy.put(ds2.getKey().trim(), aliment);

                        }
                    }
                }

                ArrayList<Aliment> local_array = new ArrayList<>();
                for (Aliment a : local_copy.values()){ // para pasarle al adapter una lista con los alimentos que ha consumido ese dia, convierte el hasmap a un array list
                    local_array.add(a);
                }

                dietFollowedAdapter = new DietFollowedAdapter(local_array,dietId,false, DietInfoActivity.this);
                RecyclerView.setAdapter(dietFollowedAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



    }

    private void getAliments(){
        db.child("users").child(auth.getUid()).child("diet").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                dietId=task.getResult().getValue(String.class);
                db.child("diets").child(task.getResult().getValue().toString()).child("aliments").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        alimentList.clear();
                        for (DataSnapshot ds : task.getResult().getChildren()) {
                            Aliment aliment = ds.getValue(Aliment.class);
                            aliment.setId(ds.getKey());
                            alimentList.add(aliment);
                            original_aliments.put(ds.getKey(),aliment);
                        }

                        dietFollowedAdapter = new DietFollowedAdapter(alimentList,dietId,true, DietInfoActivity.this);
                        RecyclerView.setAdapter(dietFollowedAdapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("OnFailureDietInfo: ","");
                        e.printStackTrace();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("OnFailureDietHistory: ","");
                e.printStackTrace();
            }
        });

    }

    private void getDietInfo(){
        db.child("users").child(auth.getUid()).child("diet").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                db.child("diets").child(snapshot.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Diet diet = snapshot.getValue(Diet.class);
                        diet_title.setText(diet.getTitle());
                        diet_description.setText(diet.getDescription());
                        dietId = diet.getId();
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

    private void toggleDietRating(Boolean like){
        String userSess = auth.getUid();
        db.child("diets").child(dietId).child("rating").child(userSess).setValue(like);
    }
}
