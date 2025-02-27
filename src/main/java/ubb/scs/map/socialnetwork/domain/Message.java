package ubb.scs.map.socialnetwork.domain;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {
    private User sender;
    private List<User> receivers;
    private String content;
    private LocalDateTime date;
    private Message reply;

    public Message(Long id, User sender, List<User> receivers, String content, LocalDateTime date,Message reply) {
        super.setId(id);
        this.sender = sender;
        this.receivers = receivers;
        this.content = content;
        this.date = date;
        this.reply = reply;
    }

    public Message(User sender, List<User> receivers, String content, LocalDateTime date,Message reply) {
        this.sender = sender;
        this.receivers = receivers;
        this.content = content;
        this.date = date;
        this.reply = reply;
    }

    public User getSender() {
        return sender;
    }
    public List<User> getReceivers() {
        return receivers;
    }
    public String getContent() {
        return content;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public Message getReply() {
        return reply;
    }

    public String toString(){
        return sender.toString() + " ; Message: " + content + " ; Date: " + date.toString();
    }

}
