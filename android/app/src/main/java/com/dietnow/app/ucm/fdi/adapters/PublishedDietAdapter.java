package com.dietnow.app.ucm.fdi.adapters;

import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dietnow.app.ucm.fdi.MainActivity;
import com.dietnow.app.ucm.fdi.R;
import com.dietnow.app.ucm.fdi.UserProfileEditActivity;
import com.dietnow.app.ucm.fdi.ViewDietActivity;
import com.dietnow.app.ucm.fdi.model.diet.Diet;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PublishedDietAdapter extends RecyclerView.Adapter<PublishedDietAdapter.ViewHolder>{
    private ArrayList<Diet> localDataSet;
    private ArrayList<Diet> allDiet;
    private Context context;
    private DatabaseReference db;
    private FirebaseAuth auth;
    private String diet_id;

    public PublishedDietAdapter(ArrayList<Diet> dataSet, String dietId, Context context) {
        localDataSet = dataSet;
        allDiet = new ArrayList<>();
        allDiet.addAll(localDataSet);
        this.context = context;
        db = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
        this.diet_id = dietId;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PublishedDietAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.published_diet_item, viewGroup, false);

        return new PublishedDietAdapter.ViewHolder(view);
    }

    public void filtrado(String filtro){
        int length=filtro.length();
        localDataSet.clear();
        if(length==0){
            localDataSet.addAll(allDiet);
        }
        else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Diet> collection =allDiet.stream().filter(i -> i.getTitle().toLowerCase().contains(filtro.toLowerCase())).collect(Collectors.toList());
                localDataSet.addAll(collection);
            }
            else{
                for (Diet u: allDiet) {
                    if(u.getTitle().toLowerCase().contains(filtro.toLowerCase())){
                        localDataSet.add(u);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(!diet_id.equalsIgnoreCase(localDataSet.get(position).getId().toString())){
            Integer visits = localDataSet.get(position).getVisits() != null ? localDataSet.get(position).getVisits().size() : 0;
            HashMap<String, Boolean> rating = localDataSet.get(position).getRating();
            Integer likes = 0;
            if(rating != null){
                for(Boolean isLike : rating.values()){
                    likes += isLike ? 1 : 0;
                }
            }

            holder.titulo.setText(localDataSet.get(position).getTitle());
            holder.descripcion.setText(localDataSet.get(position).getDescription());
            holder.id.setText(localDataSet.get(position).getId());
            holder.visit.setText(String.valueOf(visits));
            holder.likes.setText(String.valueOf(likes));
            holder.verDieta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewDietActivity.class);
                    intent.putExtra("did", holder.id.getText().toString());
                    context.startActivity(intent);
                }
            });
        }else{

        }


    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titulo;
        private final TextView descripcion;
        private final MaterialButton verDieta;
        private final TextView id;
        private final TextView visit;
        private final TextView likes;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            titulo =  view.findViewById(R.id.PDietTitulo);
            descripcion =  view.findViewById(R.id.PDietDesc);
            verDieta = view.findViewById(R.id.PDietShowBtn);
            id =  view.findViewById(R.id.PDietId);
            visit =  view.findViewById(R.id.pNVisitDiet);
            likes =  view.findViewById(R.id.pNlikesDiet);
        }
    }
}
