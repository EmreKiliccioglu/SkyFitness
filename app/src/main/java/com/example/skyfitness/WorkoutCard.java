package com.example.skyfitness;

public class WorkoutCard {
    private int iconResId;
    private String title;
    private int backgroundResId;
    private String programId;

    // Constructor'ı güncelle
    public WorkoutCard(String title, int backgroundResId, String programId) {

        this.title = title;
        this.backgroundResId = backgroundResId;
        this.programId = programId;
    }

    // Getter metodları


    public String getTitle() {
        return title;
    }

    public int getBackgroundResId() {
        return backgroundResId;
    }

    public String getProgramId(){ return programId;}
}

