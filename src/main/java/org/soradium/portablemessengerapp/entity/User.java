package org.soradium.portablemessengerapp.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "users")
public class User {
    @Id
    @Column(name = "username")
    private String username;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(
            name = "frnd_map",
            joinColumns = {
                    @JoinColumn(name = "usr_username")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "friend_username")
            }
    )
    private List<User> friends
            = new ArrayList<>(8);

    public User() {
    }

    public User(List<Message> messagesSent,
                List<Message> messagesReceived,
                List<User> friends) {
//        this.messagesSent = messagesSent;
//        this.messagesReceived = messagesReceived;
        this.friends = friends;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userId) {
        this.username = userId;
    }

//    public List<Message> getMessagesSent() {
//        return messagesSent;
//    }

//    public void setMessagesSent(List<Message> messagesSent) {
//        this.messagesSent = messagesSent;
//    }
//

    /// /    public List<Message> getMessagesReceived() {
    /// /        return messagesReceived;
    /// /    }
//
//    public void setMessagesReceived(List<Message> messagesReceived) {
//        this.messagesReceived = messagesReceived;
//    }
    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    // Think about load/unload of messages as they
    // come in (user loads chat - load messages?)
//    @ManyToMany(
//            fetch = FetchType.LAZY,
//            cascade = {}
//            // if person an admin and they delete the group
//            // consider some way to remove the group from table
//            // *** Otherwise, group exists independently of users
//    )
//    @JoinTable(
//            name = "usr_grp",
//            joinColumns = {
//                    @JoinColumn(name = "usr_username")
//            },
//            inverseJoinColumns = {
//                    @JoinColumn(name = "group_id")
//            }
//    )
//    private final List<Group> groups
//            = new ArrayList<>(8);
//    @OneToMany(

//            fetch = FetchType.LAZY,
//            cascade = {
//                    CascadeType.REMOVE
//            },
//            mappedBy = "admin"
//    )
//    private final List<Group> administratedGroups
//            = new ArrayList<>(4);
}
