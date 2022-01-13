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

import retrofit2.Retrofit;

public class AddManualFood extends AppCompatActivity {

    private Button add;
    private EditText barcode;
    private String actualDiet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_manual_food);

        // Inicializar los componentes de la vista
        add         = findViewById(R.id.btnAddFood);
        barcode     = findViewById(R.id.barcodeFood);
        actualDiet  = getIntent().getExtras().getString("did");

        // Acciones de los componentes
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bc = barcode.getText().toString();
                if(!bc.isEmpty()){
                    GetProductInfo.getInstance().getInfo(bc, actualDiet);
                    // Redirigir a la edición de la dieta
                    Intent intent = new Intent(AddManualFood.this, CreateDietActivity.class);
                    intent.putExtra("did", actualDiet);
                    startActivity(intent);
                }
            }
        });
    }
}