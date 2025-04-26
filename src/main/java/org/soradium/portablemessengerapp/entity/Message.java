package org.soradium.portablemessengerapp.entity;

import jakarta.persistence.*;

// add fetchtypes for m-m/etc. connections
@Entity(name = "message_table")
public class Message {
    @Id
    @Column(name = "msg_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long messageId;

    @Column(name = "msg_text")
    private String messageTextData;

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = {}
    )
    @JoinColumn(name = "chat_id")
    private Chat chatAssigned;

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = {}
    )
    @JoinColumn(name = "username")
    private User sender;

    public Message() {
    }

    public Message(String messageTextData, Chat chatAssigned, User sender) {
        this.messageTextData = messageTextData;
        this.chatAssigned = chatAssigned;
        this.sender = sender;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getMessageTextData() {
        return messageTextData;
    }

    public void setMessageTextData(String sentMessage) {
        this.messageTextData = sentMessage;
    }

    public Chat getChatAssigned() {
        return chatAssigned;
    }

    public void setChatAssigned(Chat chatAssigned) {
        this.chatAssigned = chatAssigned;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", sentMessage='" + messageTextData + '\'' +
                '}';
    }


}


