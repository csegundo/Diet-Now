package com.dietnow.app.ucm.fdi.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dietnow.app.ucm.fdi.MainActivity;
import com.dietnow.app.ucm.fdi.R;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.dietnow.app.ucm.fdi.model.diet.NutritionalInfo;
import com.dietnow.app.ucm.fdi.utils.GetAllProductInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DietFollowedAdapter extends RecyclerView.Adapter<DietFollowedAdapter.ViewHolder> {
    private ArrayList<Aliment> localDataSet;
    private ArrayList<Aliment> allAliments;
    private Context context;
    private DatabaseReference db;
    private FirebaseAuth auth;



    public DietFollowedAdapter(ArrayList<Aliment> dataSet, Context context) {
        localDataSet = dataSet;
        allAliments =new ArrayList<>();
        allAliments.addAll(localDataSet);
        this.context=context;
        db = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
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
        holder.kcal_info.setText(localDataSet.get(position).getKcal() + "/100g");
        holder.info_cantidad.setText("asda");
        // CHECKBOX ???

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private TextView aliment_id ,kcal_info;
        private EditText info_cantidad;


        public ViewHolder(View view) {
            super(view);

            checkBox     = view.findViewById(R.id.id_checkBox);
            aliment_id   = view.findViewById(R.id.id_aliment);
            kcal_info    = view.findViewById(R.id.id_kcal);
            info_cantidad= view.findViewById(R.id.id_cantidad);
        }
    }
}



