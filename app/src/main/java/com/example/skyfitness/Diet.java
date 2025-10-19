package com.example.skyfitness;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Diet extends AppCompatActivity {

    private LinearLayout btnAntrenman, btnOlcum, btnDiyet, btnBilgilerim;

    private EditText edtCurrentWeight, edtTargetWeight, edtHeight, edtAge, edtGoalDuration;
    private RadioGroup rgGender;
    private Spinner spinnerActivityLevel;
    private Button btnCalculate;
    private TextView txtResultCalories, txtProtein, txtCarbs, txtFat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Menü butonları
        btnAntrenman = findViewById(R.id.btnAntrenman);
        btnOlcum = findViewById(R.id.btnOlcum);
        btnDiyet = findViewById(R.id.btnDiyet);
        btnBilgilerim = findViewById(R.id.btnBilgilerim);

        // Kullanıcı giriş alanları
        edtCurrentWeight = findViewById(R.id.edtCurrentWeight);
        edtTargetWeight = findViewById(R.id.edtTargetWeight);
        edtHeight = findViewById(R.id.edtHeight);
        edtAge = findViewById(R.id.edtAge);
        edtGoalDuration = findViewById(R.id.edtGoalDuration);
        rgGender = findViewById(R.id.rgGender);
        spinnerActivityLevel = findViewById(R.id.spinnerActivityLevel);
        btnCalculate = findViewById(R.id.btnCalculate);

        // Sonuç gösterilecek TextView'ler
        txtResultCalories = findViewById(R.id.txtResultCalories);
        txtProtein = findViewById(R.id.txtProtein);
        txtCarbs = findViewById(R.id.txtCarbs);
        txtFat = findViewById(R.id.txtFat);

        // Aktivite seviyesi spinner'ı ayarla
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.activity_levels,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivityLevel.setAdapter(adapter);

        // Hesapla butonu
        btnCalculate.setOnClickListener(v -> calculateNutrition());

        // Menü butonları
        btnAntrenman.setOnClickListener(v -> {
            startActivity(new Intent(Diet.this, Workout.class));
            finish();
        });

        btnOlcum.setOnClickListener(v -> {
            startActivity(new Intent(Diet.this, Measure.class));
            finish();
        });

        btnDiyet.setOnClickListener(v -> {
            // Zaten bu ekrandayız
        });

        btnBilgilerim.setOnClickListener(v -> {
            startActivity(new Intent(Diet.this, Info.class));
            finish();
        });

        // Makro detay sayfaları
        txtProtein.setOnClickListener(v -> startActivity(new Intent(Diet.this, Protein.class)));
        txtCarbs.setOnClickListener(v -> startActivity(new Intent(Diet.this, Karbonhidrat.class)));
        txtFat.setOnClickListener(v -> startActivity(new Intent(Diet.this, Yag.class)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void calculateNutrition() {
        // Giriş değerlerini al
        String currentWeightStr = edtCurrentWeight.getText().toString();
        String targetWeightStr = edtTargetWeight.getText().toString();
        String heightStr = edtHeight.getText().toString();
        String ageStr = edtAge.getText().toString();
        String goalDurationStr = edtGoalDuration.getText().toString();

        // Boş alan kontrolü
        if (currentWeightStr.isEmpty() || targetWeightStr.isEmpty() || heightStr.isEmpty()
                || ageStr.isEmpty() || goalDurationStr.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sayılara çevir
        double currentWeight = Double.parseDouble(currentWeightStr);
        double targetWeight = Double.parseDouble(targetWeightStr);
        int height = Integer.parseInt(heightStr);
        int age = Integer.parseInt(ageStr);
        int goalDuration = Integer.parseInt(goalDurationStr);

        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        boolean isMale = (selectedGenderId == R.id.rbMale);

        String activityLevel = spinnerActivityLevel.getSelectedItem() != null ?
                spinnerActivityLevel.getSelectedItem().toString().toLowerCase() : "";

        // BMR Hesapla
        double bmr = isMale
                ? 10 * currentWeight + 6.25 * height - 5 * age + 5
                : 10 * currentWeight + 6.25 * height - 5 * age - 161;

        // Aktivite katsayısı
        double activityMultiplier = 1.2;
        if (activityLevel.contains("hafif")) {
            activityMultiplier = 1.375;
        } else if (activityLevel.contains("orta")) {
            activityMultiplier = 1.55;
        } else if (activityLevel.contains("çok")) {
            activityMultiplier = 1.725;
        } else if (activityLevel.contains("aşırı")) {
            activityMultiplier = 1.9;
        }

        double tdee = bmr * activityMultiplier;

        double weightDiff = targetWeight - currentWeight; // hedef - mevcut

        // 1 kg = 7700 kcal
        double totalCalorieDiff = weightDiff * 7700;

        // Günlük kalori farkı
        double dailyCalorieDiff = totalCalorieDiff / goalDuration;

        // Günlük kalori farkı sınırla
        if (dailyCalorieDiff > 1000) {
            dailyCalorieDiff = 1000;
        } else if (dailyCalorieDiff < -1000) {
            dailyCalorieDiff = -1000;
        }

        // Günlük kalori hedefi
        double totalCalories = tdee + dailyCalorieDiff;

        if (totalCalories < 1200) {
            Toast.makeText(this, "Uyarı: Hesaplanan günlük kalori çok düşük!", Toast.LENGTH_LONG).show();
        }

        // Makro oranları (kcal bazlı)
        double proteinRatio = 0.30;
        double carbsRatio = 0.40;
        double fatRatio = 0.30;

        // Gram bazlı makro hesapla
        double proteinGrams = (totalCalories * proteinRatio) / 4;
        double carbsGrams = (totalCalories * carbsRatio) / 4;
        double fatGrams = (totalCalories * fatRatio) / 9;

        // Sonuçları yaz
        txtResultCalories.setText(String.format("Toplam Kalori: %.0f kcal", totalCalories));
        txtProtein.setText(String.format("Protein: Günlük %.0f gr", proteinGrams));
        txtCarbs.setText(String.format("Karbonhidrat: Günlük %.0f gr", carbsGrams));
        txtFat.setText(String.format("Yağ: Günlük %.0f gr", fatGrams));
    }
}
