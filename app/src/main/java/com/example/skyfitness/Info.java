package com.example.skyfitness;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Info extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;
    private TextView tvHead;
    private RecyclerView recyclerViewUsers;
    private List<UserModel> userList = new ArrayList<>();
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        tvHead = findViewById(R.id.tvHead);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserAdapter(this, userList);
        recyclerViewUsers.setAdapter(adapter);

       // getUserDataFromFirestore();
        getAntrenörUsers();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

   /* private void getUserDataFromFirestore() {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String username = document.getString("username");
                        String lastname = document.getString("lastname");
                        tvHead.setText("Hoş Geldin " + username + " " + lastname);
                    }
                });
    }*/

    private void getAntrenörUsers() {
        db.collection("users")
                .whereEqualTo("role", "Antrenör")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String userId = doc.getId();
                        String username = doc.getString("username");
                        String lastname = doc.getString("lastname");
                        String email = doc.getString("email");
                        String role = doc.getString("role");

                        if (username != null && lastname != null) {
                            userList.add(new UserModel(userId, username, lastname, email, role));
                        }
                    }
                    adapter.setFullList(userList);
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_message, menu); // özel menü dosyası

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Antrenör Ara...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });

        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            mAuth.signOut();
            Toast.makeText(Info.this, "Çıkış yapıldı", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Info.this, Login.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/


    private static class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

        private Context context;
        private List<UserModel> userList;
        private List<UserModel> userListFull;

        public UserAdapter(Context context, List<UserModel> userList) {
            this.context = context;
            this.userList = new ArrayList<>(userList);
            this.userListFull = new ArrayList<>(userList);
        }

        public void setFullList(List<UserModel> newList) {
            this.userList = new ArrayList<>(newList);
            this.userListFull = new ArrayList<>(newList);
        }

        public void filter(String text) {
            userList.clear();
            if (text.isEmpty()) {
                userList.addAll(userListFull);
            } else {
                text = text.toLowerCase();
                for (UserModel user : userListFull) {
                    String fullName = user.getUsername() + " " + user.getLastname();
                    if (fullName.toLowerCase().contains(text) || user.getEmail().toLowerCase().contains(text)) {
                        userList.add(user);
                    }
                }
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            UserModel user = userList.get(position);
            holder.tvFullName.setText(user.getUsername() + " " + user.getLastname());
            holder.tvEmail.setText(user.getEmail());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, Messages.class);
                intent.putExtra("selectedUserId", user.getUserId());
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        public static class UserViewHolder extends RecyclerView.ViewHolder {
            TextView tvFullName, tvEmail;
            ImageView imgProfile;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                tvFullName = itemView.findViewById(R.id.tvUserFullName);
                tvEmail = itemView.findViewById(R.id.tvUserEmail);
                imgProfile = itemView.findViewById(R.id.imgProfile);
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
