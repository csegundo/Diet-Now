package com.dietnow.app.ucm.fdi.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dietnow.app.ucm.fdi.AdminPageActivity;
import com.dietnow.app.ucm.fdi.DietComments;
import com.dietnow.app.ucm.fdi.DietInfoActivity;
import com.dietnow.app.ucm.fdi.MainActivity;
import com.dietnow.app.ucm.fdi.MyDietsActivity;
import com.dietnow.app.ucm.fdi.R;
import com.dietnow.app.ucm.fdi.ViewDietActivity;
import com.dietnow.app.ucm.fdi.model.comments.Comment;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.dietnow.app.ucm.fdi.model.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private ArrayList<Comment> localDataSet;
    private ArrayList<Comment> allComments;
    private Context context;
    private DatabaseReference db;
    private FirebaseAuth auth;
    private String diet_id;

    public CommentsAdapter(ArrayList<Comment> dataSet, Context context, String diet_id) {
        this.diet_id = diet_id;
        localDataSet = dataSet;
        allComments = new ArrayList<>();
        allComments.addAll(localDataSet);
        this.context = context;
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
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.id.setText(localDataSet.get(position).getId());
        holder.comment.setText(localDataSet.get(position).getComment());

        String user_comment_id  = localDataSet.get(position).getUser();
        FirebaseUser user = auth.getCurrentUser();
        db.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!user.getUid().equalsIgnoreCase(user_comment_id) && !snapshot.child("role").getValue(String.class).equalsIgnoreCase("admin")) {
                    holder.edit.setVisibility(View.GONE);
                    holder.delete.setVisibility(View.GONE);
                    holder.comment.setFocusable(false);
                    holder.comment.setFocusableInTouchMode(false);
                    holder.comment.setInputType(InputType.TYPE_NULL);
                    holder.comment.setBackgroundColor(Color.TRANSPARENT);
                    holder.comment.setHeight(150);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.child("comments").child(diet_id).child(localDataSet.get(position).getId())
                        .child("comment").setValue(holder.comment.getText().toString())
                        ;
                Toast.makeText(context, context.getResources().getString(R.string.comment_updated_succesfully), Toast.LENGTH_SHORT).show();

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAndDeleteComment(holder);

            }
        });
    }

    private void confirmAndDeleteComment(CommentsAdapter.ViewHolder holder){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_comment)
                .setMessage(R.string.confirm_delete_comment)
                .setPositiveButton(R.string.delete_alert_yes_opt, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.child("comments").child(diet_id).child(holder.id.getText().toString()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                /*
                                allComments.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                notifyItemRangeChanged(holder.getAdapterPosition(),allComments.size());
                                notifyDataSetChanged();
                                //notifyItemRangeRemoved(holder.getAdapterPosition(),allComments.size());
                                holder.itemView.setVisibility(View.GONE);

                                 */
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.delete_alert_no_opt, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final EditText comment;
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
