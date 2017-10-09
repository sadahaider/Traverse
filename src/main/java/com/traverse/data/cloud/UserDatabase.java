package com.traverse.data.cloud;

import com.amazonaws.services.dynamodbv2.document.*;
import com.traverse.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Iterator;


@Component
public class UserDatabase {

    @Autowired
    private DBClient dbClient;

    private String tableName;

    public UserDatabase(@Value("${cloud.dynamoDB_table_name_users}") String tableName) {
        this.tableName = tableName;
    }

    public boolean doesUserExist(String username){
        ItemCollection<ScanOutcome> items = dbClient.getDynamoDB().getTable(tableName).scan(new ScanFilter(User.DB_IDENTIFIER_USERNAME).eq(username));
        return items.iterator().hasNext();
    }

    public String getUserIdFromSocial(String socialMediaID){
        ItemCollection<ScanOutcome> items = dbClient.getDynamoDB().getTable(tableName).scan(new ScanFilter(User.DB_IDENTIFIER_SOCIAL_MEDIA_ID).eq(socialMediaID));

        Iterator<Item> iterator = items.iterator();
        if (!iterator.hasNext()){
            return null;
        }
        return iterator.next().getString(User.DB_IDENTIFIER_USER_ID);
    }

    public String update(User user){
        return dbClient.getDynamoDB().getTable(tableName).putItem(Item.fromJSON(user.toJson())).toString();
    }

    public String getUserJson(String id){
        Item item = dbClient.getDynamoDB().getTable(tableName).getItem(User.DB_IDENTIFIER_USER_ID, id);
        if (item == null){
            return null;
        }
        return item.toJSON();
    }

    public User getUserByID(String id){
        return User.fromJSON(getUserJson(id));
    }


}
