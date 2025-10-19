package com.example.skyfitness;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreatePlan extends AppCompatActivity {

    private EditText etDate, etTime, etNote;
    private Button btnSavePlan;
    private FirebaseFirestore db;
    private String userId;

    private MaterialCalendarView calendarView;
    private RecyclerView recyclerPlans;
    private List<Map<String, String>> planList = new ArrayList<>();
    private PlanAdapter planAdapter;

    private String selectedFormattedDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // View'ları tanımla
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etNote = findViewById(R.id.etNote);
        btnSavePlan = findViewById(R.id.btnSavePlan);

        calendarView = findViewById(R.id.calendarView);
        recyclerPlans = findViewById(R.id.recyclerPlans);

        recyclerPlans.setLayoutManager(new LinearLayoutManager(this));
        planAdapter = new PlanAdapter(planList);
        recyclerPlans.setAdapter(planAdapter);

        userId = getIntent().getStringExtra("USER_ID");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());
        btnSavePlan.setOnClickListener(v -> savePlan());

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedFormattedDate = String.format(Locale.getDefault(), "%02d%02d%04d",
                    date.getDay(), date.getMonth() + 1, date.getYear());
            updatePlanList();
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            String selectedDate = String.format("%02d/%02d/%04d", d, m + 1, y);
            etDate.setText(selectedDate);
        }, year, month, day);

        dialog.show();
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, (view, h, m) -> {
            String selectedTime = String.format("%02d:%02d", h, m);
            etTime.setText(selectedTime);
        }, hour, minute, true);

        dialog.show();
    }

    private void savePlan() {
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty() || note.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            return;
        }

        String formattedDate = date.replaceAll("[^\\d]", "");

        Map<String, Object> event = new HashMap<>();
        event.put("time", time);
        event.put("note", note);

        db.collection("users")
                .document(userId)
                .collection("plan")
                .document(formattedDate)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<Map<String, String>> existingEvents = (List<Map<String, String>>) documentSnapshot.get("events");
                    if (existingEvents != null) {
                        for (Map<String, String> ev : existingEvents) {
                            if (ev.get("time").equals(time)) {
                                Toast.makeText(this, "Bu saatte zaten bir plan var!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

                    if (documentSnapshot.exists()) {
                        db.collection("users")
                                .document(userId)
                                .collection("plan")
                                .document(formattedDate)
                                .update("events", FieldValue.arrayUnion(event))
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Plan başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
                                    if (selectedFormattedDate != null && selectedFormattedDate.equals(formattedDate)) {
                                        updatePlanList();
                                    }
                                });
                    } else {
                        Map<String, Object> planData = new HashMap<>();
                        planData.put("events", new ArrayList<>());

                        db.collection("users")
                                .document(userId)
                                .collection("plan")
                                .document(formattedDate)
                                .set(planData)
                                .addOnSuccessListener(aVoid -> {
                                    db.collection("users")
                                            .document(userId)
                                            .collection("plan")
                                            .document(formattedDate)
                                            .update("events", FieldValue.arrayUnion(event));
                                    Toast.makeText(this, "Plan başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
                                    if (selectedFormattedDate != null && selectedFormattedDate.equals(formattedDate)) {
                                        updatePlanList();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreatePlan.this, "Hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePlanList() {
        if (selectedFormattedDate == null) return;

        db.collection("users")
                .document(userId)
                .collection("plan")
                .document(selectedFormattedDate)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    planList.clear();
                    if (documentSnapshot.exists()) {
                        List<Map<String, String>> events = (List<Map<String, String>>) documentSnapshot.get("events");
                        if (events != null) {
                            // Saat bilgisine göre sıralama
                            events.sort((e1, e2) -> {
                                String time1 = e1.get("time");
                                String time2 = e2.get("time");
                                return time1.compareTo(time2);
                            });
                            planList.addAll(events);
                        }
                    }
                    planAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Planlar alınamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
