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
                /*
                Intent intent = new Intent(context, ViewDietActivity.class);
                intent.putExtra("did", holder.id.getText().toString());
                context.startActivity(intent);
                 */
                String aliment_id = localDataSet.get(position).getId();
                db.child("diets").child(diet_id).child("aliments").child(aliment_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("Borra el alimento de la dieta");
                    }
                });
            }
        });
        holder.fullInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = localDataSet.get(position).getId();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.food_details)
                        //.setMessage("Informacion detallada aqui igual una tabla o algo yo que se")
                        .setMessage(
                                "Por cada 100g de " + "Pulpitos en aceite de oliva" + ": \n" +
                                "Valor energético:    " + "133" + " kcal \n" +
                                        "Grasas:    " + "5" + "g \n" +
                                        "   de las cuales saturadas:    " + "1" + "g \n" +
                                        "Hidratos de cabrono:    " + "1" + "g \n" +
                                        "   de los cuales azúcares:    " + "0" + "g \n" +
                                        "Proteinas:    " + "21" + "g \n" +
                                        "Sal:    " + "1,5" + "g \n"
                        )
                        .setNegativeButton(R.string.delete_alert_no_opt, null).show();

                /*
                * PROBLEMOS:
                * 1.- linea 92, la informacion nutricional no se inserta en bbdd por lo cual el getID no sirve (dejarlo asi o meter todos los campos en aliment
                *       desde el principio y fuera?)
                * 2.- no se muy bien cual es la llamada a la pagina de openfoodfacts donde se especifica los campos concretos a coger
                * Campos que se van a mostrar (a parte de los que ya tenemos)
                * Los que tenemos: String name, double grams, double kcal
                *
                * double fat, double saturatedFat, double carbs, double sugar, double proteins, double salt
                * fat_100g, saturated-fat_100g, carbohydrates_100g, sugars_100g, proteins_100g, salt_100g
                *                                                                                           -Vitali
                * */
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



