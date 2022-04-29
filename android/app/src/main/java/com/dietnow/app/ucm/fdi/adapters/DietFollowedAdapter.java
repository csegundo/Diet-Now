package com.dietnow.app.ucm.fdi.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.dietnow.app.ucm.fdi.DietInfoActivity;
import com.dietnow.app.ucm.fdi.MainActivity;
import com.dietnow.app.ucm.fdi.R;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.dietnow.app.ucm.fdi.model.diet.NutritionalInfo;
import com.dietnow.app.ucm.fdi.utils.GetAllProductInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DietFollowedAdapter extends RecyclerView.Adapter<DietFollowedAdapter.ViewHolder> {
    private ArrayList<Aliment> localDataSet;
    private ArrayList<Aliment> allAliments;
    private Context context;
    private DatabaseReference db;
    private FirebaseAuth auth;
    private String dietID;
    private ArrayList<Pair<String,ViewHolder>> alimentList_toInsert;
    private Boolean can_edit = false;



    public DietFollowedAdapter(ArrayList<Aliment> dataSet,String diet_id,Boolean can_edit, Context context) {
        localDataSet = dataSet;
        alimentList_toInsert =new ArrayList<>();
        allAliments =new ArrayList<>();
        allAliments.addAll(localDataSet);
        this.context=context;
        db = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
        auth        = FirebaseAuth.getInstance();
        dietID = diet_id;
        this.can_edit = can_edit;// para saber si solo es para visulizar los alimentos de otro dia o es para editar los alimentos del día actual
        // TODO si queremos que no se vea el checkbox habróia que ocultarlo y habría que ocultar el boton de guardar de la vista general

    }


    @Override
    public DietFollowedAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.diet_followed_aliment_item, viewGroup, false);

        return new DietFollowedAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if(!can_edit){
            holder.checkBox.setVisibility(View.GONE);
            holder.info_cantidad.setVisibility(View.GONE);
        }
        holder.aliment_id.setText(localDataSet.get(position).getName());
        holder.kcal_info.setText(localDataSet.get(position).getKcal() + " kcal/100g");
        holder.info_cantidad.setText("0");
        holder.aliment_barcode.setText(localDataSet.get(position).getId());

        if(can_edit){

            db.child("diets").child(dietID).child("aliments").child(localDataSet.get(position).getId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    holder.totalGR.setText(task.getResult().child("grams").getValue(Long.class).toString() +" gr");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("OnFailureAdapter: ","");
                    e.printStackTrace();
                }
            });

            db.child("diet_history").child(auth.getUid()).child(dietID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    LocalDateTime currentDate = LocalDateTime.now();
                    String strDate = currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                    HashMap<String, Integer> map_grams_counter = new HashMap<String, Integer>();
                    for (DataSnapshot ds : task.getResult().getChildren()) {
                        String strDate_info[] = strDate.split(" ");
                        String db_date[] = ds.getKey().split(" ");
                        if (strDate_info[0].equalsIgnoreCase(db_date[0])) {
                            for(DataSnapshot ds2 :ds.getChildren()) {
                                Integer counter = map_grams_counter.get(ds2.getKey());
                                if (counter != null) { //ya esta ese id en el mapa
                                    Integer total = counter + Integer.parseInt(ds.child(ds2.getKey()).getValue().toString());
                                    map_grams_counter.put(ds2.getKey(), total);
                                } else {
                                    map_grams_counter.put(ds2.getKey(), Integer.parseInt(ds2.getValue().toString()));
                                }
                            }
                        }
                    }

                    if(map_grams_counter.get(holder.aliment_barcode.getText().toString()) == null){
                        holder.llevasGR.setText("0");
                    }else{
                        holder.llevasGR.setText(map_grams_counter.get(holder.aliment_barcode.getText().toString()).toString());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("OnFailureCreateDiet: ","");
                    e.printStackTrace();
                }
            });
        }else{
            Double d = localDataSet.get(position).getGrams_consumed();
            holder.llevasGR.setText(d.toString());

            db.child("diets").child(dietID).child("aliments").child(localDataSet.get(position).getId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    holder.totalGR.setText(task.getResult().child("grams").getValue(Long.class).toString() +" gr");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("OnFailureAdapter: ","");
                    e.printStackTrace();
                }
            });

        }


        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBox.isChecked())
                    alimentList_toInsert.add(new Pair<String,ViewHolder>(holder.aliment_barcode.getText().toString(),holder));
                else{
                    alimentList_toInsert.remove(new Pair<String,ViewHolder>(holder.aliment_barcode.getText().toString(),holder));
                }

            }
        });
    }
    public void guardar(){
        if(!alimentList_toInsert.isEmpty()) {
            for (Pair<String, ViewHolder> data : alimentList_toInsert) {
                db.child("users").child(auth.getUid()).child("diet").addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        LocalDateTime currentDate = LocalDateTime.now();
                        String strDate = currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                        db.child("diet_history").child(auth.getUid()).child(snapshot.getValue().toString()).child(strDate).child(data.first).setValue(Integer.parseInt(data.second.info_cantidad.getText().toString()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            alimentList_toInsert.clear();
        }
        else{
            Toast.makeText(context, context.getResources().getString(R.string.alertSaveAliment), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private TextView aliment_id ,kcal_info, aliment_barcode,totalGR,llevasGR;
        private EditText info_cantidad;


        public ViewHolder(View view) {
            super(view);

            checkBox     = view.findViewById(R.id.id_checkBox);
            aliment_id   = view.findViewById(R.id.id_aliment);
            kcal_info    = view.findViewById(R.id.id_kcal);
            info_cantidad= view.findViewById(R.id.id_cantidad);
            aliment_barcode =view.findViewById(R.id.aliment_barcode);
            totalGR =view.findViewById(R.id.totalGR);
            llevasGR =view.findViewById(R.id.llevasGR);

        }
    }

    public ArrayList<Pair<String,ViewHolder>>list (){
        return alimentList_toInsert;
    }
}



