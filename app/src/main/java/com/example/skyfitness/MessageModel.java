package com.example.skyfitness;

public class MessageModel {
    private String senderId;
    private String receiverId;
    private String message;
    private long timestamp;

    // Boş constructor (Firebase için gerekli)
    public MessageModel() {}

    // Dolu constructor
    public MessageModel(String senderId, String receiverId, String message, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getter'lar
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }

    // Setter'lar (opsiyonel ama Firebase bazı durumlarda ister)
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
