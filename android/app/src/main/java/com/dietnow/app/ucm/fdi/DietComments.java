package com.dietnow.app.ucm.fdi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dietnow.app.ucm.fdi.model.comments.Comment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
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

        db.child("comments").child(actualDiet).child(commentId).setValue(c).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.created_comment_diet_successfully), Toast.LENGTH_SHORT).show();

            }
        });
    }
}