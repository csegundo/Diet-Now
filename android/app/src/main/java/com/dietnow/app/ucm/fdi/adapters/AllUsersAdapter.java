package com.dietnow.app.ucm.fdi.adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.dietnow.app.ucm.fdi.AdminPageActivity;
import com.dietnow.app.ucm.fdi.AllUserActivity;
import com.dietnow.app.ucm.fdi.MainActivity;
import com.dietnow.app.ucm.fdi.R;
import com.dietnow.app.ucm.fdi.model.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.ViewHolder> {

    private ArrayList<User> localDataSet;
    private ArrayList<User> allUser;
    private Context mcon;
    private DatabaseReference bd;
    private FirebaseAuth auth;

    public AllUsersAdapter(ArrayList<User> dataSet,Context context) {
        localDataSet = dataSet;
        allUser =new ArrayList<>();
        allUser.addAll(localDataSet);
        mcon=context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_item, viewGroup, false);

        return new ViewHolder(view);
    }

    public void filtrado(String filtro){
        int length=filtro.length();
        localDataSet.clear();
        if(length==0){
            localDataSet.addAll(allUser);
        }
        else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<User> collection =allUser.stream().filter(i -> i.getEmail().toLowerCase().contains(filtro.toLowerCase())).collect(Collectors.toList());
                localDataSet.addAll(collection);
            }
            else{
                for (User u: allUser) {
                    if(u.getEmail().toLowerCase().contains(filtro.toLowerCase())){
                        localDataSet.add(u);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull AllUsersAdapter.ViewHolder holder, int position) {
        holder.name.setText(localDataSet.get(position).getName());
        holder.email.setText(localDataSet.get(position).getEmail());
        //holder.image.setImageURI(); TODO
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcon, AdminPageActivity.class);
                showDeleteAlert(mcon,intent);

            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcon, AdminPageActivity.class);
                mcon.startActivity(intent);
            }
        });


    }
    private void showDeleteAlert(Context mcon, Intent intent){
        AlertDialog.Builder builder = new AlertDialog.Builder(mcon);
        builder.setTitle(R.string.delete_alert_title);
        builder.setMessage(R.string.delete_alert_msg)
                .setPositiveButton(R.string.delete_alert_yes_opt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //String email = findViewById(R.id.AllUserEmail);
                        //borrar al usuario

                    }
                })
                .setNegativeButton(R.string.delete_alert_no_opt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView email;
        //private final ImageView image;
        private final ImageButton edit;
        private final ImageButton delete;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            email =  view.findViewById(R.id.AllUserEmail);
            name =  view.findViewById(R.id.AllUserName);
            //image =  view.findViewById(R.id.AllUserImage);
            edit =  view.findViewById(R.id.AllUserEditBtn);
            delete =  view.findViewById(R.id.AllUserDeleteBtn);

        }
    }
}
