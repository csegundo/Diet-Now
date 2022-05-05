package com.dietnow.app.ucm.fdi.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dietnow.app.ucm.fdi.MainActivity;
import com.dietnow.app.ucm.fdi.R;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.dietnow.app.ucm.fdi.model.diet.NutritionalInfo;
import com.dietnow.app.ucm.fdi.utils.GetAllProductInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AlimentViewOnlyAdapter extends RecyclerView.Adapter<AlimentViewOnlyAdapter.ViewHolder> {
    private ArrayList<Aliment> localDataSet;
    private ArrayList<Aliment> allAliments;
    private Context context;
    private DatabaseReference db;
    private FirebaseAuth auth;
    private String diet_id;



    public AlimentViewOnlyAdapter(ArrayList<Aliment> dataSet, Context context, String diet_id) {
        this.diet_id =diet_id;
        localDataSet = dataSet;
        allAliments =new ArrayList<>();
        allAliments.addAll(localDataSet);
        this.context=context;
        db = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
    }


    @Override
    public AlimentViewOnlyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.aliment_view_only_item, viewGroup, false);

        return new AlimentViewOnlyAdapter.ViewHolder(view);

    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.id.setText(localDataSet.get(position).getId());
        holder.titulo.setText(localDataSet.get(position).getName());
        String kcal_info =  String.valueOf(localDataSet.get(position).getKcal()) + " kcal";
        holder.kcal.setText(kcal_info);
        String grams_info= String.valueOf(localDataSet.get(position).getGrams()) + " gr";
        holder.grams.setText(grams_info);
        holder.fullInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = holder.id.getText().toString();
                boolean result = name.matches("[0-9]+");
                if(result){
                    NutritionalInfo nutri = GetAllProductInfo.getInstance().getInfo(holder.id.getText().toString());
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.food_details)
                            //.setMessage("Informacion detallada aqui igual una tabla o algo yo que se")
                            .setMessage(
                                context.getResources().getString(R.string.foreach_100g) + " " + nutri.getName() + ": \n" +
                                context.getResources().getString(R.string.energetic_value) + ":    " + nutri.getKcal() + " kcal \n" +
                                context.getResources().getString(R.string.fats) + ":    " + nutri.getFat() + "g \n" +
                                "   " + context.getResources().getString(R.string.saturated_fats) + ":    " + nutri.getSaturatedFat() + "g \n" +
                                context.getResources().getString(R.string.carbohydrates) + ":    " + nutri.getCarbs() + "g \n" +
                                "   " + context.getResources().getString(R.string.sugars) + ":    " + nutri.getSugar() + "g \n" +
                                context.getResources().getString(R.string.proteins) + ":    " + nutri.getProteins() + "g \n" +
                                context.getResources().getString(R.string.salt) + ":    " + nutri.getSalt() + "g \n"
                            )
                            .setNegativeButton(R.string.delete_alert_no_opt, null).show();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.food_details)
                            //.setMessage("Informacion detallada aqui igual una tabla o algo yo que se")
                            .setMessage(
                                context.getResources().getString(R.string.foreach_100g) + " " + holder.titulo.getText().toString() + ": \n" +
                                context.getResources().getString(R.string.energetic_value) + ":    " + holder.kcal.getText().toString() + " kcal \n"
                            )
                            .setNegativeButton(R.string.delete_alert_no_opt, null).show();
                }
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
        private final ImageButton fullInfo;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            titulo =  view.findViewById(R.id.AlimentNameOnly);
            grams =  view.findViewById(R.id.AlimentGramsOnly);
            kcal =  view.findViewById(R.id.AlimentKalOnly);
            id =  view.findViewById(R.id.barcodeAlimentOnly);
            fullInfo =  view.findViewById(R.id.viewOnlyAlimentDetails);

        }
    }
}



