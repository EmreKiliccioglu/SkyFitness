package com.example.skyfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Messages extends AppCompatActivity {

    private String receiverId, senderId;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<MessageModel> messageList = new ArrayList<>();
    private MessageAdapter adapter;

    private EditText etMessage;
    private ImageView btnSend;
    private RecyclerView recyclerMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        receiverId = getIntent().getStringExtra("selectedUserId");
        mAuth = FirebaseAuth.getInstance();
        senderId = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        recyclerMessages = findViewById(R.id.recyclerMessages);
        recyclerMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messageList, senderId);
        recyclerMessages.setAdapter(adapter);

        TextView tvReceiverName = findViewById(R.id.tvReceiverName);

        db.collection("users").document(receiverId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String username = documentSnapshot.getString("username");
                String lastname = documentSnapshot.getString("lastname");
                tvReceiverName.setText(username + " " + lastname);
            }
        });

        loadMessages();

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        long timestamp = System.currentTimeMillis();
        MessageModel message = new MessageModel(senderId, receiverId, text, timestamp);

        String convoId = getConversationId(senderId, receiverId);

        db.collection("messages").document(convoId).collection("chats")
                .add(message)
                .addOnSuccessListener(doc -> etMessage.setText(""));
    }

    private void loadMessages() {
        String convoId = getConversationId(senderId, receiverId);

        db.collection("messages").document(convoId).collection("chats")
                .orderBy("timestamp")
                .addSnapshotListener((snapshots, e) -> {
                    if (snapshots != null) {
                        messageList.clear();
                        for (DocumentSnapshot doc : snapshots) {
                            MessageModel message = doc.toObject(MessageModel.class);
                            messageList.add(message);
                        }
                        adapter.notifyDataSetChanged();
                        recyclerMessages.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    private String getConversationId(String id1, String id2) {
        return (id1.compareTo(id2) < 0) ? id1 + "_" + id2 : id2 + "_" + id1;
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // bir önceki sayfaya dön
        return true;
    }
}
