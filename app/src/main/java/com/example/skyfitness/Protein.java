package com.example.skyfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Protein extends AppCompatActivity {

    private TextView txtProteinData;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protein);

        // FirebaseFirestore instance
        db = FirebaseFirestore.getInstance();

        // Verileri göstermek için TextView
        txtProteinData = findViewById(R.id.txtProteinData);

        // ActionBar ayarları
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Firestore'dan verileri çek
        loadProteinData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // bir önceki sayfaya dön
        return true;
    }

    private void loadProteinData() {
        // Firestore'daki yol
        CollectionReference proteinRef = db.collection("eats/protein/hayvansal");

        // Verileri çekme işlemi
        proteinRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            StringBuilder proteinData = new StringBuilder();

                            // Veritabanındaki her belgeyi işleme
                            for (DocumentSnapshot document : querySnapshot) {
                                String name = document.getString("name");
                                String grams = document.getString("gram");
                                String note = document.getString("note");

                                // Verileri ekrana yazdırma
                                proteinData.append("Yiyecek: ").append(name)
                                        .append("\n100 Gr: ").append(grams)
                                        .append("\nBilgi: ").append(note)
                                        .append("\n\n");
                            }

                            // Ekrana verileri yazdır
                            txtProteinData.setText(proteinData.toString());
                        }
                    } else {
                        Toast.makeText(Protein.this, "Veriler yüklenemedi.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
