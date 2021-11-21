package com.dietnow.app.ucm.fdi.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dietnow.app.ucm.fdi.R;
import com.dietnow.app.ucm.fdi.model.user.User;

import java.util.ArrayList;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.ViewHolder> {

    private ArrayList<User> localDataSet;

    public AllUsersAdapter(ArrayList<User> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllUsersAdapter.ViewHolder holder, int position) {
        holder.name.setText(localDataSet.get(position).getName());
        holder.email.setText(localDataSet.get(position).getEmail());
        //holder.image.setImageURI(); TODO

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
        //private final Button edit;
        //private final Button delete;



        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            email =  view.findViewById(R.id.AllUserEmail);
            name =  view.findViewById(R.id.AllUserName);
            //image =  view.findViewById(R.id.AllUserImage);
            //edit =  view.findViewById(R.id.AllUserEditBtn);
            //delete =  view.findViewById(R.id.AllUserDeleteBtn);
        }
    }
}
