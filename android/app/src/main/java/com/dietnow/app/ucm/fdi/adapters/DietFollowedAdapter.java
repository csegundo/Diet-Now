package com.dietnow.app.ucm.fdi.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dietnow.app.ucm.fdi.DietInfoActivity;
import com.dietnow.app.ucm.fdi.MainActivity;
import com.dietnow.app.ucm.fdi.R;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.dietnow.app.ucm.fdi.model.diet.NutritionalInfo;
import com.dietnow.app.ucm.fdi.utils.GetAllProductInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DietFollowedAdapter extends RecyclerView.Adapter<DietFollowedAdapter.ViewHolder> {
    private ArrayList<Aliment> localDataSet;
    private ArrayList<Aliment> allAliments;
    private Context context;
    private DatabaseReference db;
    private FirebaseAuth auth;
    private String dietID;
    private ArrayList<Pair<String,Integer>> alimentList_toInsert;



    public DietFollowedAdapter(ArrayList<Aliment> dataSet,String diet_id, Context context) {
        localDataSet = dataSet;
        allAliments =new ArrayList<>();
        allAliments.addAll(localDataSet);
        this.context=context;
        db = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
        auth        = FirebaseAuth.getInstance();
        dietID = diet_id;
    }


    @Override
    public DietFollowedAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.diet_followed_aliment_item, viewGroup, false);

        return new DietFollowedAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.aliment_id.setText(localDataSet.get(position).getName());
        holder.kcal_info.setText(localDataSet.get(position).getKcal() + "kcal/100g");
        holder.info_cantidad.setText("0");
        holder.aliment_barcode.setText(localDataSet.get(position).getId());

        db.child("diets").child(dietID).child("aliments").child(localDataSet.get(position).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.totalGR.setText(snapshot.child("grams").getValue(Long.class).toString() +" gr");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                db.child("users").child(auth.getUid()).child("diet").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DateFormat dateFormat = new SimpleDateFormat("y-M-d H:m:s");
                        String strDate = dateFormat.format(new Date());

                        Pair<String,Integer> p = new Pair<String,Integer>(holder.aliment_barcode.getText().toString(),Integer.parseInt(holder.info_cantidad.getText().toString()));


                        db.child("diet_history").child(auth.getUid()).child(snapshot.getValue().toString()).child(strDate).child("id_alimento").setValue(p.first);
                        db.child("diet_history").child(auth.getUid()).child(snapshot.getValue().toString()).child(strDate).child("cantidad").setValue(p.second);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




            }
        });


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

    public ArrayList<Pair<String,Integer>>list (){
        return alimentList_toInsert;
    }
}



