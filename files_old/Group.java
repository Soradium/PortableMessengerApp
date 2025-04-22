//package org.soradium.portablemessengerapp.entity;
//
//import jakarta.persistence.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
/// / add fetchtypes for m-m/etc. connections
//@Entity(name = "group_table")
//public class Group {
//    @Id
//    @Column(name = "group_id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long groupId;
//
//    @Column(name = "group_name")
//    private String groupName;
//
//    @ManyToMany(
//            fetch = FetchType.LAZY,
//            cascade = {}, //users exist independently of groups
//            mappedBy = "groups"
//    )
//    private List<User> usersAdded
//            = new ArrayList<>(8);
//
//    @OneToMany(
//            fetch = FetchType.LAZY,
//            cascade = {CascadeType.REMOVE}
//            // messages should be removed if group is removed
//            // (only admin has to have rights for this action)
//    )
//    @JoinColumn(name = "group_id")
//    private List<Message> messages
//            = new ArrayList<>(32);
//
//    @ManyToOne(
//            fetch = FetchType.EAGER,
//            cascade = {}
//    )
//    @JoinColumn(
//            name = "grp_admin"
//    )
//    private User admin;
//
//    public Group() {
//    }
//
//    public Group(List<User> usersAdded,
//                 User admin) {
//        this.usersAdded = usersAdded;
//        this.admin = admin;
//    }
//
//    public long getGroupId() {
//        return groupId;
//    }
//
//    public void setGroupId(long groupId) {
//        this.groupId = groupId;
//    }
//
//    public List<User> getUsersAdded() {
//        return usersAdded;
//    }
//
//    public void setUsersAdded(List<User> usersAdded) {
//        this.usersAdded = usersAdded;
//    }
//
//    public User getAdmin() {
//        return admin;
//    }
//
//    public void setAdmin(User admin) {
//        this.admin = admin;
//    }
//
//    public List<Message> getMessages() {
//        return messages;
//    }
//
//    public void setMessages(List<Message> messages) {
//        this.messages = messages;
//    }
//
//    public String getGroupName() {
//        return groupName;
//    }
//
//    public void setGroupName(String groupName) {
//        this.groupName = groupName;
//    }
//
//    @Override
//    public String toString() {
//        return "Group{" +
//                "groupId=" + groupId +
//                ", groupName='" + groupName + '\'' +
//                ", usersAdded=" + usersAdded +
//                ", messages=" + messages +
//                ", admin=" + admin +
//                '}';
//    }
//}
