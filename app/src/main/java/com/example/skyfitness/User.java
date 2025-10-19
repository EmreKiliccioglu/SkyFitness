package com.example.skyfitness;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.concurrent.atomic.AtomicInteger;

public class User extends AppCompatActivity {

    private TextView tvHeader,tvAraBaslik;
    private LinearLayout btnAntrenman, btnOlcum, btnDiyet, btnBilgilerim;
    private LinearLayout exerciseContainer;
    private ImageButton btnDelete;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user2);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        tvHeader = findViewById(R.id.tvHeader);
        tvAraBaslik = findViewById(R.id.tvAraBaslik);
        btnAntrenman = findViewById(R.id.btnAntrenman);
        btnOlcum = findViewById(R.id.btnOlcum);
        btnDiyet = findViewById(R.id.btnDiyet);
        btnBilgilerim = findViewById(R.id.btnBilgilerim);
        exerciseContainer = findViewById(R.id.exerciseContainer);
        btnDelete = findViewById(R.id.btnDelete);

        getUserDataFromFirestore();
        getDateFromSelectedPrograms();
        getSelectedExercisesFromFirestore();


        btnAntrenman.setOnClickListener(v -> startActivity(new Intent(User.this, Workout.class)));
        btnOlcum.setOnClickListener(v -> startActivity(new Intent(User.this, Measure.class)));
        btnDiyet.setOnClickListener(v -> startActivity(new Intent(User.this, Diet.class)));
        btnBilgilerim.setOnClickListener(v -> startActivity(new Intent(User.this, Info.class)));


        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void showDeleteConfirmationDialog() {
        // AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Egzersizleri Sil")
                .setMessage("Seçilen tüm egzersizleri silmek istediğinize emin misiniz?")
                .setPositiveButton("Evet", (dialog, which) -> deleteSelectedExercises())
                .setNegativeButton("Hayır", (dialog, which) -> dialog.dismiss())
                .setCancelable(false) // Kullanıcı dışarıya tıklayarak kapatamasın
                .show();
    }

    private void getUserDataFromFirestore() {
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String username = document.getString("username");
                            String lastname = document.getString("lastname");
                            if (username != null) {
                                tvHeader.setText("Hoş Geldin " + username + " " + lastname );
                            } else {
                                tvHeader.setText("");
                            }
                        }
                    } else {
                        Toast.makeText(User.this, "Kullanıcı verileri alınamadı", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getSelectedExercisesFromFirestore() {
        db.collection("users")
                .document(userId)
                .collection("selected_exercises")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            TextView emptyMessage = new TextView(this);
                            emptyMessage.setText("Henüz seçilmiş egzersiz yok.");
                            emptyMessage.setGravity(Gravity.CENTER);
                            exerciseContainer.addView(emptyMessage);
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String exerciseName = document.getId();

                                TextView exerciseView = new TextView(this);
                                exerciseView.setText("EGZERSİZ : " + exerciseName);
                                exerciseView.setTextColor(Color.parseColor("#FFFFFF"));
                                exerciseView.setTextSize(16);
                                exerciseView.setPadding(32, 32, 32, 32);
                                exerciseView.setBackgroundColor(Color.parseColor("#1C4E80"));

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                params.setMargins(0, 24, 0, 0);
                                exerciseView.setLayoutParams(params);

                                LinearLayout inputLayout = new LinearLayout(this);
                                inputLayout.setOrientation(LinearLayout.VERTICAL);

                                EditText kgInput = new EditText(this);
                                kgInput.setHint("Kilogram (kg)");
                                kgInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                                kgInput.setPadding(16, 16, 16, 16);
                                inputLayout.addView(kgInput);

                                // Setler için EditText'leri yan yana ekle
                                LinearLayout setLayout = new LinearLayout(this);
                                setLayout.setOrientation(LinearLayout.HORIZONTAL); // Yatay sıralama

                                EditText[] repsInputs = new EditText[4];
                                for (int i = 0; i < 4; i++) {
                                    TextView setText = new TextView(this);
                                    setText.setText("Set " + (i + 1));
                                    setText.setTextSize(14);
                                    setLayout.addView(setText);

                                    EditText repsInput = new EditText(this);
                                    repsInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                                    repsInput.setPadding(16, 16, 16, 16);

                                    // Her bir EditText'i yatayda eşit olarak dağıtmak için LayoutParams kullan
                                    LinearLayout.LayoutParams repsParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                                    repsInput.setLayoutParams(repsParams);

                                    setLayout.addView(repsInput);

                                    repsInputs[i] = repsInput;
                                }

                                inputLayout.addView(setLayout);

                                Button saveButton = new Button(this);
                                saveButton.setText("Kaydet");
                                saveButton.setTextColor(Color.parseColor("#FFFFFF"));
                                saveButton.setBackgroundColor(Color.parseColor("#1C4E80"));



                                saveButton.setOnClickListener(v -> {
                                    String kg = kgInput.getText().toString();
                                    String[] reps = new String[4];
                                    boolean allFieldsFilled = true;

                                    for (int i = 0; i < 4; i++) {
                                        reps[i] = repsInputs[i].getText().toString();
                                        if (reps[i].isEmpty()) {
                                            allFieldsFilled = false;
                                        }
                                    }

                                    if (kg.isEmpty() || !allFieldsFilled) {
                                        Toast.makeText(User.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                                    } else {
                                        saveExerciseData(exerciseName, kg, reps);
                                    }
                                });

                                inputLayout.addView(saveButton);

                                String kgValue = document.getString("kg");
                                String[] repsValues = new String[4];
                                for (int i = 0; i < 4; i++) {
                                    repsValues[i] = document.getString("set" + (i + 1) + "_reps");
                                }

                                if (kgValue != null) {
                                    kgInput.setText(kgValue);
                                }
                                for (int i = 0; i < 4; i++) {
                                    if (repsValues[i] != null) {
                                        repsInputs[i].setText(repsValues[i]);
                                    }
                                }

                                exerciseContainer.addView(exerciseView);
                                exerciseContainer.addView(inputLayout);
                            }
                        }
                    } else {
                        Toast.makeText(User.this, "Egzersizler alınamadı", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveExerciseData(String exerciseName, String kg, String[] reps) {
        db.collection("users")
                .document(userId)
                .collection("selected_exercises")
                .document(exerciseName)
                .update(
                        "kg", kg,
                        "set1_reps", reps[0],
                        "set2_reps", reps[1],
                        "set3_reps", reps[2],
                        "set4_reps", reps[3]
                )
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(User.this, "Egzersiz verisi kaydedildi", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(User.this, "Veri kaydedilemedi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteSelectedExercises() {
        db.collection("users")
                .document(userId)
                .collection("selected_exercises")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalDocuments = task.getResult().size();
                        AtomicInteger deletedDocuments = new AtomicInteger(0);

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("users")
                                    .document(userId)
                                    .collection("selected_exercises")
                                    .document(document.getId())
                                    .delete()
                                    .addOnCompleteListener(deleteTask -> {
                                        if (deleteTask.isSuccessful()) {
                                            int currentDeleted = deletedDocuments.incrementAndGet();  // Atomic olarak artır

                                            // Eğer tüm belgeler silindiyse, "Egzersizler silindi" mesajını bir kere göster
                                            if (currentDeleted == totalDocuments) {
                                                Toast.makeText(User.this, "Egzersizler silindi", Toast.LENGTH_SHORT).show();
                                                refreshScreen();  // Ekranı yenile
                                            }
                                        } else {
                                            Toast.makeText(User.this, "Silme işlemi başarısız", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(User.this, "Veriler alınamadı", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getDateFromSelectedPrograms() {
        db.collection("users")
                .document(userId)
                .collection("selected_programs")
                .document("tarih")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String date = documentSnapshot.getString("date");
                        if (date != null) {
                            tvAraBaslik.setText("Antrenmanın Seçilme Tarihi: " + date);
                        } else {
                            tvAraBaslik.setText("Tarih bilgisi yok");
                        }
                    } else {
                        tvAraBaslik.setText("Doküman bulunamadı");
                    }
                })
                .addOnFailureListener(e -> {
                    tvAraBaslik.setText("Hata: " + e.getMessage());
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        exerciseContainer.removeAllViews();
        getSelectedExercisesFromFirestore();
    }
    private void refreshScreen() {
        exerciseContainer.removeAllViews();
        getSelectedExercisesFromFirestore();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            mAuth.signOut();
            Toast.makeText(User.this, "Çıkış yapıldı", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(User.this, Login.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
