package com.example.skyfitness;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WorkoutPagerAdapter extends RecyclerView.Adapter<WorkoutPagerAdapter.WorkoutViewHolder> {

    private List<WorkoutCard> workoutList;
    private Context context;

    public WorkoutPagerAdapter(List<WorkoutCard> workoutList, Context context) {
        this.workoutList = workoutList;
        this.context = context;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_workout_card, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutCard card = workoutList.get(position);
        holder.cardText.setText(card.getTitle());


        holder.itemView.setBackgroundResource(card.getBackgroundResId());

        holder.itemView.setOnClickListener(v -> {
            String title = card.getTitle();
            String programId = card.getProgramId();

            if (title.equals("Güçlü Adımlarla Başla")) {
                // GirisSeviye.java ekranına yönlendirme
                Intent intent = new Intent(context, GirisSeviye.class);
                intent.putExtra("programId", programId);
                context.startActivity(intent);
            }
            else if (title.equals("Sınırları Zorla")) {
                // GirisSeviye.java ekranına yönlendirme
                Intent intent = new Intent(context, GirisSeviye.class);
                intent.putExtra("programId", programId);
                context.startActivity(intent);
            }
            else if (title.equals("Kilodan Kaç")) {
                // GirisSeviye.java ekranına yönlendirme
                Intent intent = new Intent(context, GirisSeviye.class);
                intent.putExtra("programId", programId);
                context.startActivity(intent);
            }else {

                Toast.makeText(context, title + " seçildi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {

        TextView cardText;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            cardText = itemView.findViewById(R.id.cardText);
        }
    }
}
