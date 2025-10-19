package com.example.skyfitness;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GirisSeviye extends AppCompatActivity {

    private LinearLayout layoutContainer;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference exerciseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sky Fitness");
        }

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        layoutContainer = new LinearLayout(this);
        layoutContainer.setOrientation(LinearLayout.VERTICAL);
        layoutContainer.setPadding(32, 32, 32, 32);
        scrollView.addView(layoutContainer);

        setContentView(scrollView);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String programId = getIntent().getStringExtra("programId");
        if (programId == null || programId.isEmpty()) {
            Toast.makeText(GirisSeviye.this, "Program ID bulunamadı.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        exerciseRef = db.collection("programs")
                .document(programId)
                .collection("exercise");

        Button selectButton = new Button(this);
        selectButton.setText("Seç");
        selectButton.setTextColor(Color.WHITE);
        selectButton.setBackgroundColor(Color.parseColor("#1C4E80"));
        selectButton.setPadding(50, 20, 50, 20);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(0, 40, 0, 0);
        selectButton.setLayoutParams(buttonParams);

        selectButton.setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                CollectionReference userExercisesRef = db.collection("users")
                        .document(userId)
                        .collection("selected_exercises");

                userExercisesRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {

                            exerciseRef.get().addOnCompleteListener(exerciseTask -> {
                                if (exerciseTask.isSuccessful()) {
                                    for (DocumentSnapshot doc : exerciseTask.getResult()) {
                                        Map<String, Object> data = doc.getData();
                                        if (data != null) {
                                            userExercisesRef.document(doc.getId()).set(data);
                                        }
                                    }

                                    // 🔁 Tarihi ayrı bir yerde sakla: /users/{userId}/selected_programs/{programId}
                                    Map<String, Object> dateData = new HashMap<>();
                                    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                    dateData.put("date", currentDate);

                                    db.collection("users")
                                            .document(userId)
                                            .collection("selected_programs")
                                            .document("tarih")
                                            .set(dateData)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(GirisSeviye.this,
                                                        "Egzersizler başarıyla kaydedildi.",
                                                        Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(GirisSeviye.this,
                                                        "Tarih kaydedilemedi: " + e.getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            });

                                } else {
                                    Toast.makeText(GirisSeviye.this,
                                            "Egzersizleri çekerken hata oluştu: " + exerciseTask.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            Toast.makeText(GirisSeviye.this,
                                    "Önce mevcut antrenman programınızı siliniz.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(GirisSeviye.this,
                                "Verileri kontrol ederken hata oluştu: " + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(GirisSeviye.this,
                        "Kullanıcı oturumu bulunamadı.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        exerciseRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String documentName = document.getId();
                        String notes = document.getString("bilgi");

                        LinearLayout cardLayout = new LinearLayout(GirisSeviye.this);
                        cardLayout.setOrientation(LinearLayout.VERTICAL);
                        cardLayout.setPadding(40, 40, 40, 40);
                        cardLayout.setBackgroundColor(Color.WHITE);

                        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        cardParams.setMargins(0, 0, 0, 40);
                        cardLayout.setLayoutParams(cardParams);
                        cardLayout.setElevation(12f);
                        cardLayout.setBackground(getDrawable(R.drawable.card_background));

                        TextView titleText = new TextView(GirisSeviye.this);
                        titleText.setText(documentName);
                        titleText.setTextSize(18);
                        titleText.setTypeface(null, Typeface.BOLD);
                        titleText.setTextColor(Color.parseColor("#1C4E80"));

                        TextView notesText = new TextView(GirisSeviye.this);
                        notesText.setText(notes != null ? notes : "Açıklama bulunamadı.");
                        notesText.setTextSize(14);
                        notesText.setTextColor(Color.parseColor("#757575"));
                        notesText.setPadding(0, 8, 0, 0);

                        cardLayout.addView(titleText);
                        cardLayout.addView(notesText);

                        cardLayout.setClickable(true);
                        cardLayout.setFocusable(true);
                        cardLayout.setOnClickListener(v -> {
                            Intent intent = new Intent(GirisSeviye.this, Egzersiz.class);
                            intent.putExtra("docId", documentName);
                            intent.putExtra("programId", programId);
                            startActivity(intent);
                        });

                        layoutContainer.addView(cardLayout);
                    }

                    layoutContainer.addView(selectButton);

                } else {
                    Toast.makeText(GirisSeviye.this,
                            "Veriler alınamadı: " + task.getException(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
