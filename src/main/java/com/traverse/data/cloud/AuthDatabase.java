package com.traverse.data.cloud;


import com.amazonaws.services.dynamodbv2.document.Item;
import com.traverse.data.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthDatabase {

    @Autowired
    private DBClient dbClient;

    @Autowired
    private UserDatabase userDatabase;

    private String tableName;

    public static final String DB_IDENTIFIER_TOKEN = "token";
    public static final String DB_IDENTIFIER_SOCIAL_MEDIA_ID = "social_media_id";
    public static final String DB_IDENTIFIER_USER_ID = "social_media_id";

    public AuthDatabase(@Value("${cloud.dynamoDB_table_name_auth}") String tableName) {
        this.tableName = tableName;
    }

    public void set(String token, String socialMediaID){
        JSONObject jsonObject = new JSONObject()
                .put(DB_IDENTIFIER_TOKEN, token)
                .put(DB_IDENTIFIER_SOCIAL_MEDIA_ID, socialMediaID)
                .put(DB_IDENTIFIER_USER_ID, userDatabase.getUserIdFromSocial(socialMediaID));

        if (jsonObject.getString(DB_IDENTIFIER_USER_ID) == null){
            User user = new User.Builder()
                    .withSocialMediaID(socialMediaID)
                    .build();
            userDatabase.update(user);
            jsonObject.put(DB_IDENTIFIER_USER_ID, user.getUserID());
        }

        dbClient.getDynamoDB().getTable(tableName).putItem(Item.fromJSON(jsonObject.toString()));
    }

    public String getUserID(String token){
        Item item = dbClient.getDynamoDB().getTable(tableName).getItem(DB_IDENTIFIER_TOKEN, token);
        if (item == null){
            return null;
        }
        return new JSONObject(item.toJSON()).getString(DB_IDENTIFIER_USER_ID);
    }



}
