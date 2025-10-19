package com.example.skyfitness;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Egzersiz extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Egzersiz Detayı");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));


        LinearLayout layoutContainer = new LinearLayout(this);
        layoutContainer.setOrientation(LinearLayout.VERTICAL);
        layoutContainer.setPadding(32, 32, 32, 32);
        layoutContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        layoutContainer.setBackgroundColor(Color.parseColor("#F5F5F5"));
        scrollView.addView(layoutContainer);

        setContentView(scrollView);


        TextView titleTextView = new TextView(this);
        titleTextView.setText("Yükleniyor...");
        titleTextView.setTextSize(22);
        titleTextView.setTextColor(Color.parseColor("#1C4E80"));
        titleTextView.setTypeface(null, Typeface.BOLD);
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setPadding(0, 0, 0, 16);
        layoutContainer.addView(titleTextView);


        ImageView imageView1 = new ImageView(this);
        LinearLayout.LayoutParams imageParams1 = new LinearLayout.LayoutParams(1000, 1000);
        imageParams1.setMargins(0, 0, 0, 24);
        imageView1.setLayoutParams(imageParams1);
        imageView1.setBackgroundResource(R.drawable.image_border);
        layoutContainer.addView(imageView1);


        LinearLayout youtubeCard = new LinearLayout(this);
        youtubeCard.setOrientation(LinearLayout.VERTICAL);
        youtubeCard.setPadding(32, 32, 32, 32);
        youtubeCard.setBackgroundColor(Color.WHITE);
        youtubeCard.setElevation(8f); // Gölge için (yalnızca API 21+)
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 16);
        youtubeCard.setLayoutParams(cardParams);


        TextView linkTitle = new TextView(this);
        linkTitle.setText("YouTube Link");
        linkTitle.setTextSize(18);
        linkTitle.setTypeface(null, Typeface.BOLD);
        linkTitle.setTextColor(Color.parseColor("#1C4E80"));
        linkTitle.setPadding(0, 0, 0, 8);
        youtubeCard.addView(linkTitle);


        TextView linkTextView = new TextView(this);
        linkTextView.setText("Yükleniyor...");
        linkTextView.setTextSize(16);
        linkTextView.setTextColor(Color.parseColor("#1C4E80"));
        linkTextView.setBackgroundResource(R.drawable.link_background);
        linkTextView.setGravity(Gravity.CENTER);
        linkTextView.setPadding(24, 16, 24, 16);
        linkTextView.setClickable(true);
        youtubeCard.addView(linkTextView);

        layoutContainer.addView(youtubeCard);



        LinearLayout notesCard = new LinearLayout(this);
        notesCard.setOrientation(LinearLayout.VERTICAL);
        notesCard.setPadding(32, 32, 32, 32);
        notesCard.setBackgroundColor(Color.WHITE);
        notesCard.setElevation(8f);
        LinearLayout.LayoutParams notesParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        notesParams.setMargins(0, 0, 0, 16);
        notesCard.setLayoutParams(notesParams);


        TextView notesTitle = new TextView(this);
        notesTitle.setText("Notlar");
        notesTitle.setTextSize(18);
        notesTitle.setTypeface(null, Typeface.BOLD);
        notesTitle.setTextColor(Color.parseColor("#1C4E80"));
        notesTitle.setPadding(0, 0, 0, 8);
        notesCard.addView(notesTitle);


        TextView notesTextView = new TextView(this);
        notesTextView.setText("Yükleniyor...");
        notesTextView.setTextSize(16);
        notesTextView.setTextColor(Color.DKGRAY);
        notesTextView.setPadding(24, 16, 24, 16);
        notesTextView.setBackgroundResource(R.drawable.notes_background);
        notesCard.addView(notesTextView);

        layoutContainer.addView(notesCard);



        db = FirebaseFirestore.getInstance();

        String docId = getIntent().getStringExtra("docId");
        String programId = getIntent().getStringExtra("programId");


        DocumentReference docRef = db.collection("programs")
                .document(programId)
                .collection("exercise")
                .document(docId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String link = document.getString("Link");
                        String notes = document.getString("notes");
                        String title = document.getId();
                        String imageUrl = document.getString("image");

                        titleTextView.setText(title);

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            new ImageLoaderTask(imageView1).execute(imageUrl);
                        }

                        linkTextView.setText(link);
                        linkTextView.setOnClickListener(v -> {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                            startActivity(browserIntent);
                        });

                        notesTextView.setText(notes);
                    } else {
                        Toast.makeText(Egzersiz.this, "Belge bulunamadı", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Egzersiz.this, "Hata: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private static class ImageLoaderTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView;

        public ImageLoaderTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String urlString = strings[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
