package com.example.skyfitness;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OlcumlerFragment extends Fragment {

    private FirebaseFirestore db;
    private String selectedUserId;
    private RecyclerView recyclerView;
    private MeasurementAdapter adapter;
    private List<MeasurementModel> measurementList;

    private TextView tvEmptyMessage;

    public OlcumlerFragment(String userId) {
        this.selectedUserId = userId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_olcumler, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMeasurements);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);
        db = FirebaseFirestore.getInstance();
        measurementList = new ArrayList<>();
        adapter = new MeasurementAdapter(measurementList);
        recyclerView.setAdapter(adapter);

        loadMeasurements();

        return view;
    }

    private void loadMeasurements() {
        db.collection("users")
                .document(selectedUserId)
                .collection("measurements")
                .orderBy("date")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    measurementList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Timestamp timestamp = documentSnapshot.getTimestamp("date");
                            String date = "";
                            if (timestamp != null) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                                date = sdf.format(timestamp.toDate());
                            }
                            String weight = documentSnapshot.getString("kilogram");
                            String arm = documentSnapshot.getString("arm");
                            String waist = documentSnapshot.getString("waist");
                            String leg = documentSnapshot.getString("leg");

                            measurementList.add(new MeasurementModel(date, weight, arm, waist, leg));
                        }
                        tvEmptyMessage.setVisibility(View.GONE);
                    } else {
                        // Ölçüm verisi yoksa boş mesajı göster
                        tvEmptyMessage.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    tvEmptyMessage.setVisibility(View.VISIBLE);
                    tvEmptyMessage.setText("Veriler alınırken hata oluştu.");
                });
    }


    // İç model sınıfı
    private static class MeasurementModel {
        String date, weight, arm, waist, leg;

        public MeasurementModel(String date, String weight, String arm, String waist, String leg) {
            this.date = date;
            this.weight = weight;
            this.arm = arm;
            this.waist = waist;
            this.leg = leg;
        }
    }

    // Adapter sınıfı
    private static class MeasurementAdapter extends RecyclerView.Adapter<MeasurementAdapter.ViewHolder> {

        private final List<MeasurementModel> measurementList;

        public MeasurementAdapter(List<MeasurementModel> measurementList) {
            this.measurementList = measurementList;
        }

        @NonNull
        @Override
        public MeasurementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.measurement_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MeasurementAdapter.ViewHolder holder, int position) {
            MeasurementModel measurement = measurementList.get(position);
            holder.tvDate.setText("Tarih: " + measurement.date);
            holder.tvWeight.setText("Kilo: " + measurement.weight);
            holder.tvArm.setText("Kol: " + measurement.arm);
            holder.tvWaist.setText("Bel: " + measurement.waist);
            holder.tvLeg.setText("Bacak: " + measurement.leg);

            // ✨ Animasyonu burada başlat
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_right);
            holder.itemView.startAnimation(animation);
        }


        @Override
        public int getItemCount() {
            return measurementList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate, tvWeight, tvArm, tvWaist, tvLeg;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tvMeasurementDate);
                tvWeight = itemView.findViewById(R.id.tvMeasurementWeight);
                tvArm = itemView.findViewById(R.id.tvMeasurementArm);
                tvWaist = itemView.findViewById(R.id.tvMeasurementWaist);
                tvLeg = itemView.findViewById(R.id.tvMeasurementLeg);
            }
        }
    }
}
