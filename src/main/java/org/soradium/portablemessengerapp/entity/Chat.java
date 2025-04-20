package org.soradium.portablemessengerapp.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Chat {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = {}
    )
    private User firstUser;

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = {}
    )
    private User secondUser;
    // consider later to add messages into chat as a dynamic load

    @OneToMany(
            mappedBy = "chatAssigned",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private List<Message> messages
            = new ArrayList<>(30);

    public Chat() {
    }

    public Chat(User firstUser, User secondUser, List<Message> messages) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.messages = messages;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(User firstUser) {
        this.firstUser = firstUser;
    }

    public User getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(User secondUser) {
        this.secondUser = secondUser;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
