package com.example.skyfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Yag extends AppCompatActivity {

    private TextView txtYagData;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yag);

        // FirebaseFirestore instance
        db = FirebaseFirestore.getInstance();

        // Verileri göstermek için TextView
        txtYagData = findViewById(R.id.txtYagData);

        // ActionBar ayarları
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Firestore'dan verileri çek
        loadYagData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // bir önceki sayfaya dön
        return true;
    }

    private void loadYagData() {
        // Firestore'daki yol
        CollectionReference yagRef = db.collection("eats/yağ/healthy");

        // Verileri çekme işlemi
        yagRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            StringBuilder yagData = new StringBuilder();

                            // Veritabanındaki her belgeyi işleme
                            for (DocumentSnapshot document : querySnapshot) {
                                String name = document.getString("name");
                                String grams = document.getString("gram");
                                String note = document.getString("note");

                                // Verileri ekrana yazdırma
                                yagData.append("Yiyecek: ").append(name)
                                        .append("\n100 Gr: ").append(grams)
                                        .append("\nBilgi: ").append(note)
                                        .append("\n\n");
                            }

                            // Ekrana verileri yazdır
                            txtYagData.setText(yagData.toString());
                        }
                    } else {
                        Toast.makeText(Yag.this, "Veriler yüklenemedi.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
