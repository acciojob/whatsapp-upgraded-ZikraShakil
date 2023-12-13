package com.driver;

import java.text.ParseException;
import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashMap<String,User> userMobile;
    private int customGroupCount ;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception {

        if(userMobile.containsKey(mobile)){
            throw new Exception("User already exists");
        }
        User user = new User(name,mobile);
        userMobile.put(mobile,user);
        return "SUCCESS";
    }

    public Group creteGroup(List<User> users) throws Exception {

        List<User> al = new ArrayList<>(users);
        if(users.size() < 2){
            throw new RuntimeException("Minimum User should be two");
        }else if(users.size() == 2) {
            User ur = al.get(1);

            Group gr = new Group(ur.getName(),2);
            adminMap.put(gr,users.get(0));
            groupUserMap.put(gr,al);
            return gr;
        }else {
            customGroupCount++;
            Group gr = new Group("Group " + customGroupCount, users.size());
            groupUserMap.put(gr, al);
            adminMap.put(gr, users.get(0));
            return gr;
        }

    }

    public int createMessage(String content) {
        Message ms = new Message(messageId,content);
        messageId++;
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        int numberOfMessage = 0;
        if((groupUserMap.containsKey(group)) == false){
            throw new RuntimeException("Group does not exist");
        }
        else if((groupUserMap.get(group).contains(sender)) == false){
            throw new RuntimeException("You are not allowed to send message");
        }
        else {
            List<Message> msg =new ArrayList<>();
            if(groupMessageMap.containsKey(group)) {
                msg = groupMessageMap.get(group);
            }
            msg.add(message);
            groupMessageMap.put(group,msg);
            numberOfMessage = msg.size();
            senderMap.put(message,sender);
        }
        return numberOfMessage;
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {

        if(!groupUserMap.containsKey(group)) {
            throw new Exception("Group does not exist");
        }else if(!adminMap.get(group).equals(approver)) {
            throw new Exception("Approver does not have rights");
        }else if(!groupUserMap.get(group).contains(user)){
            throw new Exception("User is not a participant");
        }else {
            adminMap.put(group,user);
            return "SUCCESS";
        }
    }

    public int removeUser(User user) {
        boolean userFound = false;
        Group userGroup = null;
        for(Group currGroup : groupUserMap.keySet()){
            List<User> currGroupMembers = groupUserMap.get(currGroup);
            if(currGroupMembers.contains(user)){
                userFound = true;
                userGroup = currGroup;

            }
        }
        if(!userFound){
            throw new RuntimeException("User not found");
        }

        if(adminMap.get(userGroup) == user){
            throw new RuntimeException("Cannot remove admin");
        }

        groupUserMap.get(userGroup).remove(user);

        List<Message> listOfMessagesToBeRemoved = new ArrayList<>();
        for(Message currMessage : senderMap.keySet()){
            if(senderMap.get(currMessage) == user){
                listOfMessagesToBeRemoved.add(currMessage);
            }
        }

        for(Message message : listOfMessagesToBeRemoved){
            groupMessageMap.get(userGroup).remove(message);
            senderMap.remove(message);
        }

        userMobile.remove(user.getMobile());
        userGroup.setNumberOfParticipants(groupUserMap.get(userGroup).size());

        return groupUserMap.get(userGroup).size() + groupMessageMap.get(userGroup).size() + senderMap.size();
    }

    public String findMessage(Date start, Date end, int k) {
        return "";
    }
}