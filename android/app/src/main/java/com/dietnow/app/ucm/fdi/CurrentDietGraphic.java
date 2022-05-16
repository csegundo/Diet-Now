package com.dietnow.app.ucm.fdi;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.dietnow.app.ucm.fdi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CurrentDietGraphic extends AppCompatActivity {

    private AnyChartView graphic;
    private DatabaseReference db;
    private FirebaseAuth auth;
    private String diet_id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_diet_graphic);

        auth       = FirebaseAuth.getInstance();
        db         = FirebaseDatabase.getInstance(MainActivity.FIREBASE_DB_URL).getReference();

        diet_id  = getIntent().getExtras().getString("diet_id");

        generateGraphicChart();
        APIlib.getInstance().setActiveAnyChartView(graphic);
    }


    private void generateGraphicChart(){
        graphic      = findViewById(R.id.dietchart);

        db.child("diet_history").child(auth.getCurrentUser().getUid()).child(diet_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Cartesian cartesian = AnyChart.line();
                cartesian.animation(true);

                cartesian.crosshair().enabled(true);
                cartesian.crosshair()
                        .yLabel(true)
                        // TODO ystroke
                        .yStroke((Stroke) null, null, null, (String) null, (String) null);

                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

                cartesian.title(getResources().getString(R.string.my_graphic));

                cartesian.yAxis(0).title("kcal");
                cartesian.xAxis(0).title(getResources().getString(R.string.day));
                cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

                List<DataEntry> seriesData = new ArrayList<>();

                HashMap<String,Double>map = new HashMap<String, Double>();

                for(DataSnapshot ds : task.getResult().getChildren()) {
                    String date = ds.getKey().split(" ")[0].trim();
                    Double total = 0.0;
                    for(DataSnapshot ds2 :ds.getChildren()){
                        total += ds2.getValue(Double.class);
                    }
                    if(map.containsKey(date)){
                        Double d = map.get(date) + total;
                        map.put(date,d);
                    }else{
                        map.put(date,total);
                    }

                }

                for(Map.Entry<String, Double> entry : map.entrySet()){
                    seriesData.add(new CurrentDietGraphic.CustomDataEntry(entry.getKey(), entry.getValue()));
                }

                Set set = Set.instantiate();
                set.data(seriesData);
                Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

                Line series1 = cartesian.line(series1Mapping);
                series1.name(getResources().getString(R.string.progress));
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

                graphic.setChart(cartesian);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("OnFailureCrrntGraph: ","");
                e.printStackTrace();
            }
        });

    }

    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value) {
            super(x, value);
        }
    }


}
