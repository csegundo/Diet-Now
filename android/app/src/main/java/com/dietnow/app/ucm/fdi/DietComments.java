package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dietnow.app.ucm.fdi.adapters.AlimentViewOnlyAdapter;
import com.dietnow.app.ucm.fdi.adapters.CommentsAdapter;
import com.dietnow.app.ucm.fdi.model.comments.Comment;
import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Las dietas tienen ID en vez de ser un Array para poder editar/borrar de manera mas directa
 */
public class DietComments extends AppCompatActivity {

    private String actualDiet;
    private FirebaseAuth auth;
    private DatabaseReference db;
    private EditText comment;
    private Button commentBtn;
    private CommentsAdapter commentsAdapter;
    private ArrayList<Comment> commentList;
    private androidx.recyclerview.widget.RecyclerView RecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_comments);

        // Atributos Firebase
        auth        = FirebaseAuth.getInstance();
        db          = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();

        // Atributos de la vista
        actualDiet  = getIntent().getExtras().getString("did");
        comment     = findViewById(R.id.newCommentText);
        commentBtn  = findViewById(R.id.newCommentBtn);
        RecyclerView  = findViewById(R.id.allDietComments);
        commentList = new ArrayList<Comment>();

        RecyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comment.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.emptyComment), Toast.LENGTH_SHORT).show();
                } else{
                    uploadComment();
                }
            }
        });


        getComments();
    }

    /**
     * FUNCIONES
     */
    private void uploadComment(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d H:m:s");
        String created = dateFormat.format(new Date());
        String commentId = db.child("comments").child(actualDiet).push().getKey();
        Comment c = new Comment(auth.getUid(), comment.getText().toString(), created);
        c.setId(commentId);

        /*
        db.child("comments").child(actualDiet).child(commentId).setValue(c).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.created_comment_diet_successfully), Toast.LENGTH_SHORT).show();
            }
        });
        */
        db.child("comments").child(actualDiet).child(commentId).setValue(c).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.created_comment_diet_successfully), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("OnFailureDietComments: ","");
                e.printStackTrace();
            }
        });
    }


    private void getComments(){
        /*
        db.child("comments").child(actualDiet).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Comment comment = ds.getValue(Comment.class);
                    comment.setId(ds.getKey());
                    commentList.add(comment);
                    //System.out.println(comment);
                }

                commentsAdapter = new CommentsAdapter(commentList,DietComments.this, actualDiet);
                RecyclerView.setAdapter(commentsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

         */
        db.child("comments").child(actualDiet).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot ds : task.getResult().getChildren()){
                    Comment comment = ds.getValue(Comment.class);
                    comment.setId(ds.getKey());
                    commentList.add(comment);
                }

                commentsAdapter = new CommentsAdapter(commentList,DietComments.this, actualDiet);
                RecyclerView.setAdapter(commentsAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("OnFailureDietComments: ","");
                e.printStackTrace();
            }
        });
    }
}