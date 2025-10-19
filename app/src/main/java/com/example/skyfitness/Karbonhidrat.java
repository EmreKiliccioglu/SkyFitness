package com.example.skyfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Karbonhidrat extends AppCompatActivity {

    private TextView txtKarbonhidratData;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karbonhidrat);

        // FirebaseFirestore instance
        db = FirebaseFirestore.getInstance();

        // Verileri göstermek için TextView
        txtKarbonhidratData = findViewById(R.id.txtKarbonhidratData);

        // ActionBar ayarları
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Firestore'dan verileri çek
        loadKarbonhidratData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // bir önceki sayfaya dön
        return true;
    }

    private void loadKarbonhidratData() {
        // Firestore'daki yol
        CollectionReference karbonhidratRef = db.collection("eats/karbonhidrat/tahıl");

        // Verileri çekme işlemi
        karbonhidratRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            StringBuilder karbonhidratData = new StringBuilder();

                            // Veritabanındaki her belgeyi işleme
                            for (DocumentSnapshot document : querySnapshot) {
                                String name = document.getString("name");
                                String grams = document.getString("gram");
                                String note = document.getString("note");

                                // Verileri ekrana yazdırma
                                karbonhidratData.append("Yiyecek: ").append(name)
                                        .append("\n100 Gr: ").append(grams)
                                        .append("\nBilgi: ").append(note)
                                        .append("\n\n");
                            }

                            // Ekrana verileri yazdır
                            txtKarbonhidratData.setText(karbonhidratData.toString());
                        }
                    } else {
                        Toast.makeText(Karbonhidrat.this, "Veriler yüklenemedi.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
