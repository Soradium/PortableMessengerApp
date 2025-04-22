//package org.soradium.portablemessengerapp.service;
//
//import jakarta.persistence.EntityManager;
//import org.soradium.portablemessengerapp.entity.Group;
//import org.soradium.portablemessengerapp.repository.GroupRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class GroupServiceImpl {
//    private GroupRepository repository;
//    private EntityManager em;
//
//    public GroupServiceImpl() {
//    }
//
//    public GroupServiceImpl(
//            GroupRepository repository) {
//        this.repository = repository;
//    }
//
//    public Group getGroupByName(String groupName) {
//        return repository
//                .findGroupByGroupName(groupName)
//                .orElse(null);
//    }
//
//    public Group createGroup(Group group) {
//        return repository.save(group);
//    }
//
//    public Group updateGroup(Group group) {
//        return repository.save(group);
//    }
//
//    public void deleteGroupById(Long groupId) {
//        repository.deleteById(groupId);
//    }
//
//    public Group findGroupById(Long groupId) {
//        return repository.findById(groupId).orElse(null);
//    }
//
//    public List<Group> findAllGroups() {
//        return repository.findAll();
//    }
//
//    public GroupRepository getRepository() {
//        return repository;
//    }
//
//    @Autowired
//    public void setRepository(
//            GroupRepository repository) throws Exception {
//        if (repository != null) {
//            this.repository = repository;
//        } else {
//            throw new Exception(
//                    "No repository " +
//                            "bean found!");
//        }
//    }
//
//    public Group findGroupWithMessagesById(long groupId) {
//        Group g = findGroupById(groupId);
//        if (g != null) {
//            g.getMessages().size();
//        }
//        return g;
//    }
//
//    public Group findGroupWithUsersById(long groupId) {
//        Group g = findGroupById(groupId);
//        if (g != null) {
//            g.getUsersAdded().size();
//        }
//        return g;
//    }
//
//    public Group findGroupWithUsersAndMessagesById(long groupId) {
//        Group g = findGroupById(groupId);
//        if (g != null) {
//            g.getUsersAdded().size();
//            g.getMessages().size();
//        }
//        return g;
//    }
//
//    @Autowired
//    public void setEntityManager(
//            EntityManager em) throws Exception {
//        if (em != null) {
//            this.em = em;
//        } else {
//            throw new Exception(
//                    "No entity manager " +
//                            "bean found!");
//        }
//    }
//}
