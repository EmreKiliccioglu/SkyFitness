package com.example.skyfitness;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class AthleteDetailsActivity extends AppCompatActivity {

    private String userId; // Kullanıcının ID'si (gizli)
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_details);

        // Kullanıcı ID'sini al
        userId = getIntent().getStringExtra("selectedUserId"); // ID'yi al ama göstermiyoruz
        tabLayout = findViewById(R.id.tabLayout);

        // Sekmeleri ekle
        tabLayout.addTab(tabLayout.newTab().setText("Antrenmanlar"));
        tabLayout.addTab(tabLayout.newTab().setText("Ölçümler"));

        // Varsayılan olarak Antrenmanlar sekmesini göster
        loadFragment(new AntrenmanlarFragment(userId));

        // Sekme tıklama dinleyicisi
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                Fragment selectedFragment = null;
                if (tab.getPosition() == 0) {
                    // Antrenmanlar sekmesine tıklandığında
                    selectedFragment = new AntrenmanlarFragment(userId);
                } else if (tab.getPosition() == 1) {
                    // Ölçümler sekmesine tıklandığında
                    selectedFragment = new OlcumlerFragment(userId);
                }
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }
            }

            @Override
            public void onTabUnselected(@NonNull TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(@NonNull TabLayout.Tab tab) {}
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadFragment(Fragment fragment) {
        // ID'yi fragment'e taşımak için bundle kullan
        Bundle bundle = new Bundle();
        bundle.putString("selectedUserId", userId);
        fragment.setArguments(bundle);

        // Fragment'ı yükle
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainer, fragment);
        transaction.commit();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
