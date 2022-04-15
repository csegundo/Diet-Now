package com.dietnow.app.ucm.fdi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.dietnow.app.ucm.fdi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CurrentDietGraphic extends AppCompatActivity {

    private AnyChartView graphic;
    private DatabaseReference db;
    private FirebaseAuth auth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_diet_graphic);

        auth       = FirebaseAuth.getInstance();
        db         = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();

        generateGraphicChart();
        //APIlib.getInstance().setActiveAnyChartView(steps);

    }


    private void generateGraphicChart(){
        graphic      = findViewById(R.id.dietchart);
        //APIlib.getInstance().setActiveAnyChartView(steps);
        db.child("diet_history").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Cartesian cartesian = AnyChart.line();
                cartesian.animation(true);

                cartesian.crosshair().enabled(true);
                cartesian.crosshair()
                        .yLabel(true)
                        // TODO ystroke
                        .yStroke((Stroke) null, null, null, (String) null, (String) null);

                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

                cartesian.title("Mis pasos");

                cartesian.yAxis(0).title("Numero de pasos");
                cartesian.xAxis(0).title("Dia");
                cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

                List<DataEntry> seriesData = new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren()){
                    String pasos = ds.getValue().toString();

                    String fecha = ds.getKey();
                    seriesData.add(new UserProfileActivity.CustomDataEntry(fecha, Integer.valueOf(pasos)));
                }

                Set set = Set.instantiate();
                set.data(seriesData);
                Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

                Line series1 = cartesian.line(series1Mapping);
                series1.name("Progreso");
                series1.hovered().markers().enabled(true);
                series1.hovered().markers()
                        .type(MarkerType.CIRCLE)
                        .size(4d);
                series1.tooltip()
                        .position("right")
                        .anchor(Anchor.LEFT_CENTER)
                        .offsetX(5d)
                        .offsetY(5d);

                cartesian.legend().enabled(true);
                cartesian.legend().fontSize(13d);
                cartesian.legend().padding(0d, 0d, 10d, 0d);

                steps.setChart(cartesian);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateStepsChart(){
        steps      = findViewById(R.id.dietchart);
        //APIlib.getInstance().setActiveAnyChartView(steps);
        db.child("pasos").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Cartesian cartesian = AnyChart.line();
                cartesian.animation(true);

                cartesian.crosshair().enabled(true);
                cartesian.crosshair()
                        .yLabel(true)
                        // TODO ystroke
                        .yStroke((Stroke) null, null, null, (String) null, (String) null);

                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

                cartesian.title("Mis pasos");

                cartesian.yAxis(0).title("Numero de pasos");
                cartesian.xAxis(0).title("Dia");
                cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

                List<DataEntry> seriesData = new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren()){
                    String pasos = ds.getValue().toString();

                    String fecha = ds.getKey();
                    seriesData.add(new UserProfileActivity.CustomDataEntry(fecha, Integer.valueOf(pasos)));
                }

                Set set = Set.instantiate();
                set.data(seriesData);
                Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

                Line series1 = cartesian.line(series1Mapping);
                series1.name("Progreso");
                series1.hovered().markers().enabled(true);
                series1.hovered().markers()
                        .type(MarkerType.CIRCLE)
                        .size(4d);
                series1.tooltip()
                        .position("right")
                        .anchor(Anchor.LEFT_CENTER)
                        .offsetX(5d)
                        .offsetY(5d);

                cartesian.legend().enabled(true);
                cartesian.legend().fontSize(13d);
                cartesian.legend().padding(0d, 0d, 10d, 0d);

                steps.setChart(cartesian);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
