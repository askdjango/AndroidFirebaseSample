package kr.festi.androidfirebasesample;


public class ChatMessage {
    public String message;
    public String name;
    public String photoUrl;

    public ChatMessage() {
    }

    public ChatMessage(String message, String name, String photoUrl) {
        this.message = message;
        this.name = name;
        this.photoUrl = photoUrl;
    }
}
