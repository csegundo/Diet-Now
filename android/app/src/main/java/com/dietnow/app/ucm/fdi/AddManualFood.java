package com.dietnow.app.ucm.fdi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dietnow.app.ucm.fdi.model.diet.Aliment;
import com.dietnow.app.ucm.fdi.utils.GetProductInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Retrofit;

public class AddManualFood extends AppCompatActivity {

    private Button add;
    private Button addManual;
    private EditText barcode;
    private EditText name;
    private EditText kcal;
    private EditText grams;
    private String actualDiet;
    private FirebaseAuth auth;
    private DatabaseReference db;
    private StorageReference storageRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_manual_food);

        // Inicializar los componentes de la vista
        add = findViewById(R.id.btnAddFood);
        addManual = findViewById(R.id.btnAddManual);
        barcode = findViewById(R.id.barcodeFood);
        name = findViewById(R.id.addName);
        kcal = findViewById(R.id.addKcal);
        grams = findViewById(R.id.addGrams);
        actualDiet = getIntent().getExtras().getString("did");

        auth        = FirebaseAuth.getInstance();
        db          = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();
        storageRef  = FirebaseStorage.getInstance().getReference(); // crear una instancia a la referencia del almacenamiento

        // Acciones de los componentes
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bc = barcode.getText().toString();
                if (!bc.isEmpty()) {
                    GetProductInfo.getInstance().getInfo(bc, actualDiet);
                    // Redirigir a la edición de la dieta
                    Intent intent = new Intent(AddManualFood.this, CreateDietActivity.class);
                    intent.putExtra("did", actualDiet);
                    startActivity(intent);
                }
            }
        });
        addManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!name.getText().toString().isEmpty() && !kcal.getText().toString().isEmpty() && !grams.getText().toString().isEmpty()){
                    String pname = "";
                    try {
                        pname = name.getText().toString();
                        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
                        md5Digest.update(pname.getBytes());
                        byte[] digest = md5Digest.digest();
                        StringBuffer hexString = new StringBuffer();
                        for (int i = 0; i < digest.length; i++) {
                            hexString.append(Integer.toHexString(0xFF & digest[i]));
                        }
                        pname = hexString.toString();

                        Aliment aliment = new Aliment(name.getText().toString(), Double.parseDouble(grams.getText().toString()), Double.parseDouble(kcal.getText().toString()));
                        db.child("aliments").child(pname).setValue(aliment);
                        db.child("diets").child(actualDiet).child("aliments").child(pname).setValue(aliment);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }
}