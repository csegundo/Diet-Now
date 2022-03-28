package com.dietnow.app.ucm.fdi.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dietnow.app.ucm.fdi.AddManualFood;
import com.dietnow.app.ucm.fdi.CameraActivity;
import com.dietnow.app.ucm.fdi.CreateDietActivity;
import com.dietnow.app.ucm.fdi.MainActivity;
import com.dietnow.app.ucm.fdi.MyDietsActivity;
import com.dietnow.app.ucm.fdi.R;
import com.dietnow.app.ucm.fdi.ViewDietActivity;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.dietnow.app.ucm.fdi.model.diet.Diet;
import com.dietnow.app.ucm.fdi.model.diet.NutritionalInfo;
import com.dietnow.app.ucm.fdi.utils.GetAllProductInfo;
import com.dietnow.app.ucm.fdi.utils.GetProductInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.api.SystemParameterOrBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AlimentsAdapter extends RecyclerView.Adapter<AlimentsAdapter.ViewHolder> {
    private ArrayList<Aliment> localDataSet;
    private ArrayList<Aliment> allAliments;
    private Context context;
    private DatabaseReference db;
    private FirebaseAuth auth;
    private String diet_id;



    public AlimentsAdapter(ArrayList<Aliment> dataSet, Context context,String diet_id) {
        this.diet_id =diet_id;
        localDataSet = dataSet;
        allAliments =new ArrayList<>();
        allAliments.addAll(localDataSet);
        this.context=context;
        db = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
    }


    @Override
    public AlimentsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.aliment_item, viewGroup, false);

        return new AlimentsAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.id.setText(localDataSet.get(position).getId());
        holder.titulo.setText(localDataSet.get(position).getName());
        holder.kcal.setText(String.valueOf(localDataSet.get(position).getKcal()));
        holder.grams.setText( String.valueOf(localDataSet.get(position).getGrams()));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aliment_id = localDataSet.get(position).getId();
                db.child("diets").child(diet_id).child("aliments").child(aliment_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("Borra el alimento de la dieta");
                        /*
                        Intent intent = new Intent(context, CreateDietActivity.class);
                        intent.putExtra("did", holder.id.getText().toString());
                        context.startActivity(intent);
                        */
                    }
                });
            }
        });
        holder.fullInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NutritionalInfo nutri = GetAllProductInfo.getInstance().getInfo(holder.id.getText().toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.food_details)
                        //.setMessage("Informacion detallada aqui igual una tabla o algo yo que se")
                        .setMessage(
                                "Por cada 100g de " + nutri.getName() + ": \n" +
                                "Valor energético:    " + nutri.getKcal() + " kcal \n" +
                                        "Grasas:    " + nutri.getFat() + "g \n" +
                                        "   de las cuales saturadas:    " + nutri.getSaturatedFat() + "g \n" +
                                        "Hidratos de cabrono:    " + nutri.getCarbs() + "g \n" +
                                        "   de los cuales azúcares:    " + nutri.getSugar() + "g \n" +
                                        "Proteinas:    " + nutri.getProteins() + "g \n" +
                                        "Sal:    " + nutri.getSalt() + "g \n"
                        )
                        .setNegativeButton(R.string.delete_alert_no_opt, null).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titulo;
        private final TextView kcal;
        private final TextView grams;
        private final TextView id;
        private final ImageButton delete;
        private final ImageButton fullInfo;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            titulo =  view.findViewById(R.id.AlimentName);
            grams =  view.findViewById(R.id.AlimentGrams);
            kcal =  view.findViewById(R.id.AlimentKal);
            id =  view.findViewById(R.id.barcodeAliment); // de la vista aliment_item_test
            delete =  view.findViewById(R.id.deleteAlimentBtn);
            fullInfo =  view.findViewById(R.id.viewAlimentDetails);

        }
    }
}



