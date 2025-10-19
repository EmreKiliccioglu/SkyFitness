package com.example.skyfitness;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class Workout extends AppCompatActivity {

    private LinearLayout btnAntrenman, btnOlcum, btnDiyet, btnBilgilerim;
    private Button btnBeginner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);


        btnAntrenman = findViewById(R.id.btnAntrenman);
        btnOlcum = findViewById(R.id.btnOlcum);
        btnDiyet = findViewById(R.id.btnDiyet);
        btnBilgilerim = findViewById(R.id.btnBilgilerim);


        ImageView leftArrow = findViewById(R.id.leftArrow);
        ImageView rightArrow = findViewById(R.id.rightArrow);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        rightArrow.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            viewPager.setCurrentItem(currentItem + 1, true);
        });

        leftArrow.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1, true);
            }
        });

        List<WorkoutCard> workoutCards = new ArrayList<>();
        workoutCards.add(new WorkoutCard( "Güçlü Adımlarla Başla", R.drawable.fitness_1 , "entry_level"));
        workoutCards.add(new WorkoutCard( "Sınırları Zorla", R.drawable.fitness_2 , "intermediate_level"));
        workoutCards.add(new WorkoutCard( "Kilodan Kaç", R.drawable.fitness_3, "advanced_level"));


        WorkoutPagerAdapter adapter = new WorkoutPagerAdapter(workoutCards, this);
        viewPager.setAdapter(adapter);


        btnAntrenman.setOnClickListener(v -> {
            Intent intent = new Intent(Workout.this, Workout.class);
            startActivity(intent);
        });

        btnOlcum.setOnClickListener(v -> {
            Intent intent = new Intent(Workout.this, Measure.class);
            startActivity(intent);
        });

        btnDiyet.setOnClickListener(v -> {
            Intent intent = new Intent(Workout.this, Diet.class);
            startActivity(intent);
        });

        btnBilgilerim.setOnClickListener(v -> {
            Intent intent = new Intent(Workout.this, Info.class);
            startActivity(intent);
        });


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
