package com.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class WhatsappService {

    WhatsappRepository wp = new WhatsappRepository();

    public String createUser(String name, String mobile) throws Exception {
        return wp.createUser(name,mobile);
    }

    public Group createGroup(List<User> users) throws Exception {
        return wp.creteGroup(users);
    }

    public int createMessage(String content) {
        return wp.createMessage(content);
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        return wp.sendMessage(message,sender,group);
    }

    public String changeAdmin(User approver, User user, Group group)  throws Exception{
        return wp.changeAdmin(approver,user,group);
    }

    public int removeUser(User user) {
        return wp.removeUser(user);
    }

    public String findMessage(Date start, Date end, int k) {
        return wp.findMessage(start,end,k);
    }
}