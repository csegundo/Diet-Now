package com.dietnow.app.ucm.fdi.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.dietnow.app.ucm.fdi.model.comments.Comment;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private ArrayList<Comment> localDataSet;
    private ArrayList<Comment> allComments;
    private Context context;
    private DatabaseReference db;
    private FirebaseAuth auth;
    private String diet_id;

    public CommentsAdapter(ArrayList<Comment> dataSet, Context context,String diet_id) {
        this.diet_id =diet_id;
        localDataSet = dataSet;
        allComments =new ArrayList<>();
        allComments.addAll(localDataSet);
        this.context= context;
        auth     = FirebaseAuth.getInstance();
        db       = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.comment_item, viewGroup, false);

        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder,@SuppressLint("RecyclerView") int position) {
        holder.id.setText(localDataSet.get(position).getId());
        holder.comment.setText(localDataSet.get(position).getComment());

        String user_comment_id  = localDataSet.get(position).getUser();
        FirebaseUser user = auth.getCurrentUser();

        if(!user.getUid().equalsIgnoreCase(user_comment_id)) {
            holder.edit.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //redirigir a la vista de insertar un comenrtario
                String auto_key = db.child("comments").child(holder.id.getText().toString()).getKey();
                System.out.println("Esta es la auto key"+ auto_key);
                //intent

            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_comment_id  = localDataSet.get(position).getUser();



            }
        });

    }


    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView comment;
        private final TextView id;
        private final Button delete;
        private final Button edit;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View


            comment =  view.findViewById(R.id.commentItemText);
            id =  view.findViewById(R.id.commentItem); // de la vista aliment_item_test
            delete =  view.findViewById(R.id.deleteComment);
            edit =  view.findViewById(R.id.editComment);

        }
    }
}
