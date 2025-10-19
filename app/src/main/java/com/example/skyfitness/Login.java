package com.example.skyfitness;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                DocumentReference userRef = db.collection("users").document(userId);

                                userRef.get().addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        UserModel userModel = documentSnapshot.toObject(UserModel.class);



                                        if (userModel != null) {
                                            String role = userModel.getRole();
                                            Log.d("Login", "Role: " + role);


                                            Intent intent;
                                            if ("Antrenör".equals(role)) {
                                                intent = new Intent(Login.this, Coach.class);
                                                intent.putExtra("userName", userModel.getUsername());
                                                intent.putExtra("lastName", userModel.getLastname());
                                                intent.putExtra("userEmail", userModel.getEmail());// Coach.java'ya yönlendir
                                            } else {
                                                intent = new Intent(Login.this, User.class); // User.java'ya yönlendir
                                                intent.putExtra("userName", userModel.getUsername());
                                                intent.putExtra("lastName", userModel.getLastname());
                                                intent.putExtra("userEmail", userModel.getEmail());
                                            }
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(Login.this, "Kullanıcı bilgisi alınamadı", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(Login.this, "Kullanıcı bilgileri bulunamadı", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(Login.this, "Kullanıcı bilgileri alınamadı", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } else {
                            Toast.makeText(Login.this, "Email veya şifre yanlış!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }
}
