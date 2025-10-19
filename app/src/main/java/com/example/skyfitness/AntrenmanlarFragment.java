package com.example.skyfitness;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AntrenmanlarFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private List<ExerciseModel> exerciseList;
    private FirebaseFirestore db;
    private String selectedUserId;
    private TextView noExercisesTextView;

    public AntrenmanlarFragment(String userId) {
        this.selectedUserId = userId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_antrenmanlar, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewExercises);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        exerciseList = new ArrayList<>();
        adapter = new ExerciseAdapter(exerciseList);
        recyclerView.setAdapter(adapter);

        // TextView ekle: "Henüz Antrenman Seçilmemiş"
        noExercisesTextView = view.findViewById(R.id.noExercisesTextView);

        // Fragment view'e animasyon ekle
        view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_right));

        loadExercises();

        return view;
    }

    private void loadExercises() {
        db.collection("users")
                .document(selectedUserId)
                .collection("selected_exercises")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // Eğer koleksiyon boşsa
                        noExercisesTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        // Eğer koleksiyon varsa ve doluysa
                        noExercisesTextView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        exerciseList.clear();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            String name = doc.getId();
                            String kg = doc.getString("kg");
                            String set1 = doc.getString("set1_reps");
                            String set2 = doc.getString("set2_reps");
                            String set3 = doc.getString("set3_reps");
                            String set4 = doc.getString("set4_reps");

                            exerciseList.add(new ExerciseModel(name, kg, set1, set2, set3, set4));
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Veri yüklenirken bir hata oluştu", Toast.LENGTH_SHORT).show());
    }

    // İç model sınıfı
    private static class ExerciseModel {
        String name, kg, set1, set2, set3, set4;

        public ExerciseModel(String name, String kg, String set1, String set2, String set3, String set4) {
            this.name = name;
            this.kg = kg;
            this.set1 = set1;
            this.set2 = set2;
            this.set3 = set3;
            this.set4 = set4;
        }
    }

    // İç adapter sınıfı
    private static class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

        private final List<ExerciseModel> exerciseList;

        public ExerciseAdapter(List<ExerciseModel> exerciseList) {
            this.exerciseList = exerciseList;
        }

        @NonNull
        @Override
        public ExerciseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExerciseAdapter.ViewHolder holder, int position) {
            ExerciseModel exercise = exerciseList.get(position);

            // Animasyonu uygula
            Animation slideIn = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_right);
            holder.itemView.startAnimation(slideIn);

            holder.tvName.setText(exercise.name);
            holder.tvKg.setText("KG: " + exercise.kg);
            holder.tvSets.setText("Setler: " + exercise.set1 + ", " + exercise.set2 + ", " +
                    exercise.set3 + ", " + exercise.set4);
        }

        @Override
        public int getItemCount() {
            return exerciseList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvKg, tvSets;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvExerciseName);
                tvKg = itemView.findViewById(R.id.tvExerciseKg);
                tvSets = itemView.findViewById(R.id.tvExerciseSets);
            }
        }
    }
}
