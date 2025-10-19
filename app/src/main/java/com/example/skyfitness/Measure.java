package com.example.skyfitness;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Measure extends AppCompatActivity {

    private LinearLayout btnAntrenman, btnOlcum, btnDiyet, btnBilgilerim;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    private EditText etKilogram, etArm, etWaist, etLeg;
    private Button btnSave;
    private LinearLayout llMeasurementHistory;
    private TextView tvLastMeasurement;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        btnAntrenman = findViewById(R.id.btnAntrenman);
        btnOlcum = findViewById(R.id.btnOlcum);
        btnDiyet = findViewById(R.id.btnDiyet);
        btnBilgilerim = findViewById(R.id.btnBilgilerim);

        etKilogram = findViewById(R.id.etKilogram);
        etArm = findViewById(R.id.etArm);
        etWaist = findViewById(R.id.etWaist);
        etLeg = findViewById(R.id.etLeg);
        btnSave = findViewById(R.id.btnSave);
        llMeasurementHistory = findViewById(R.id.llMeasurementHistory);
        tvLastMeasurement = findViewById(R.id.tvLastMeasurement);

        getMeasurementHistory(); // Sayfa açıldığında geçmişi göster

        btnAntrenman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Measure.this, Workout.class));
                finish();
            }
        });

        // Ölçüm butonu tıklanınca Measurement ekranına geç
        btnOlcum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Diyet butonuna tıklanırsa hiçbir şey yapma (zaten bu sayfadayız)
        btnDiyet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Measure.this, Diet.class));
                finish();
            }
        });

        // Bilgilerim butonu tıklanınca Profile ekranına geç
        btnBilgilerim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Measure.this, Info.class));
                finish();
            }
        });

        btnSave.setOnClickListener(v -> {
            String kilogram = etKilogram.getText().toString().trim();
            String arm = etArm.getText().toString().trim();
            String waist = etWaist.getText().toString().trim();
            String leg = etLeg.getText().toString().trim();

            if (!kilogram.isEmpty() && !arm.isEmpty() && !waist.isEmpty() && !leg.isEmpty()) {
                saveMeasurementToFirestore(kilogram, arm, waist, leg);
            } else {
                Toast.makeText(Measure.this, "Lütfen tüm ölçüleri girin", Toast.LENGTH_SHORT).show();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }



    private void saveMeasurementToFirestore(String kilogram, String arm, String waist, String leg) {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        Map<String, Object> measurement = new HashMap<>();
        measurement.put("kilogram", kilogram);
        measurement.put("arm", arm);
        measurement.put("waist", waist);
        measurement.put("leg", leg);
        measurement.put("date", new Date());
        measurement.put("userId", userId);

        db.collection("users")
                .document(userId)
                .collection("measurements")
                .add(measurement)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Measure.this, "Ölçüm kaydedildi", Toast.LENGTH_SHORT).show();
                        etKilogram.setText("");
                        etArm.setText("");
                        etWaist.setText("");
                        etLeg.setText("");
                        getMeasurementHistory(); // Güncel listeyi yeniden çek
                    } else {
                        Toast.makeText(Measure.this, "Ölçüm kaydedilemedi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getMeasurementHistory() {
        db.collection("users")
                .document(userId)
                .collection("measurements")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        llMeasurementHistory.removeAllViews();
                        boolean isFirst = true;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String kilogram = document.getString("kilogram");
                            String arm = document.getString("arm");
                            String waist = document.getString("waist");
                            String leg = document.getString("leg");
                            Date dateObj = document.getDate("date");
                            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(dateObj);

                            if (isFirst) {
                                tvLastMeasurement.setText("Son ölçüm: " + kilogram + " kg, Kol: " + arm + " cm, Bel: " + waist + " cm, Bacak: " + leg + " cm");
                                isFirst = false;
                            }

                            // Kart benzeri görünüm
                            LinearLayout cardLayout = new LinearLayout(this);
                            cardLayout.setOrientation(LinearLayout.VERTICAL);
                            cardLayout.setPadding(30, 20, 30, 20);
                            cardLayout.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(0, 0, 0, 20);
                            cardLayout.setLayoutParams(params);

                            TextView dateText = new TextView(this);
                            dateText.setText("Tarih: " + date);
                            dateText.setTextSize(18f);
                            dateText.setTextColor(Color.parseColor("#1C4E80"));

                            TextView valuesText = new TextView(this);
                            valuesText.setText("Kilo: " + kilogram + " kg\nKol: " + arm + " cm\nBel: " + waist + " cm\nBacak: " + leg + " cm");
                            valuesText.setTextSize(16f);
                            valuesText.setPadding(0, 10, 0, 0);

                            cardLayout.addView(dateText);
                            cardLayout.addView(valuesText);

                            llMeasurementHistory.addView(cardLayout);
                        }

                        if (isFirst) {
                            tvLastMeasurement.setText("Son ölçüm: -- kg, -- cm, -- cm, -- cm");
                        }

                    } else {
                        Toast.makeText(Measure.this, "Ölçüm geçmişi alınamadı", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
