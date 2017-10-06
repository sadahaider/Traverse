package com.traverse.data.cloud;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.traverse.data.User;
import com.traverse.exceptions.UserDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class UserDatabase {

    @Autowired
    private DBClient dbClient;

    private String tableName;

    public UserDatabase(@Value("${cloud.dynamoDB_table_name_users}") String tableName) {
        this.tableName = tableName;
    }

    public void create(User user){
        try {
            getUser(user.getUsername());
        } catch (UserDoesNotExistException e){
            update(user);
            return;
        }
        throw new IllegalStateException("User already exists.");
    }

    public void update(User user){
        dbClient.getMapper().save(user);
    }

    public User getUser(String username) throws UserDoesNotExistException {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":val", new AttributeValue().withS(username));
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(tableName)
                .withFilterExpression(User.DB_IDENTIFIER_USERNAME + " = :val")
                .withExpressionAttributeValues(expressionAttributeValues);
        ScanResult result = dbClient.getClient().scan(scanRequest);

        if (result.getItems().size() == 0){
            throw new UserDoesNotExistException("No user under the name: " + username);
        }

        if (result.getItems().size() > 1){
            throw new IllegalStateException("Multiple users with the name: " + username);
        }

        String id = result.getItems().get(0).get(User.DB_IDENTIFIER_USER_ID).getS();
        return dbClient.getMapper().load(User.class, id);
    }

}
