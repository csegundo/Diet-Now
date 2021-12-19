package com.dietnow.app.ucm.fdi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dietnow.app.ucm.fdi.MainActivity;
import com.dietnow.app.ucm.fdi.R;
import com.dietnow.app.ucm.fdi.model.diet.Diet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyDietsAdapter extends RecyclerView.Adapter<MyDietsAdapter.ViewHolder>{
    private ArrayList<Diet> localDataSet;
    private ArrayList<Diet> allDiet;
    private Context context;
    private DatabaseReference db;
    private FirebaseAuth auth;

    public MyDietsAdapter(ArrayList<Diet> dataSet, Context context) {
        localDataSet = dataSet;
        allDiet =new ArrayList<>();
        allDiet.addAll(localDataSet);
        this.context=context;
        db = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyDietsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_item, viewGroup, false);

        return new MyDietsAdapter.ViewHolder(view);
    }

    public void filtrado(String filtro){
        int length=filtro.length();
        localDataSet.clear();
        if(length==0){
            localDataSet.addAll(allDiet);
        }
        else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Diet> collection =allDiet.stream().filter(i -> i.getTitulo().toLowerCase().contains(filtro.toLowerCase())).collect(Collectors.toList());
                localDataSet.addAll(collection);
            }
            else{
                for (Diet u: allDiet) {
                    if(u.getTitulo().toLowerCase().contains(filtro.toLowerCase())){
                        localDataSet.add(u);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull MyDietsAdapter.ViewHolder holder, int position) {
        holder.titulo.setText(localDataSet.get(position).getTitulo());
        holder.descripcion.setText(localDataSet.get(position).getDescripcion());
        holder.verDieta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }

        });


    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titulo;
        private final TextView descripcion;
        private final Button verDieta;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            titulo =  view.findViewById(R.id.dietDesc);
            descripcion =  view.findViewById(R.id.DietTitulo);

            verDieta =  view.findViewById(R.id.myDietShowBtn);

        }
    }
}
