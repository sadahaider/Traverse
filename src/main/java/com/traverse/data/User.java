package com.traverse.data;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.traverse.exceptions.UsernameException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User object
 *
 * Holds data pertaining to the user profile.
 *
 */
@DynamoDBDocument
@DynamoDBTable(tableName="traverse_users")
public class User {

    private static final Log logger = LogFactory.getLog(User.class);

    public static final String
            DB_IDENTIFIER_USER_ID = "user_id",
            DB_IDENTIFIER_REGISTER_TIME = "register_time",
            DB_IDENTIFIER_AUDIO_LIST= "audio_list",
            DB_IDENTIFIER_USERNAME = "username";

    private String username; //Unique identifier

    private String userID; //Unique identifier key

    private Long registerTime;

    private List<String> audioIdList;

    @DynamoDBHashKey(attributeName=DB_IDENTIFIER_USER_ID)
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

    @DynamoDBAttribute(attributeName=DB_IDENTIFIER_USERNAME)
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @DynamoDBAttribute(attributeName=DB_IDENTIFIER_REGISTER_TIME)
    public Long getRegisterTime() {
        return registerTime;
    }
    public void setRegisterTime(Long registerTime) {
        this.registerTime = registerTime;
    }

    @DynamoDBAttribute(attributeName=DB_IDENTIFIER_AUDIO_LIST)
    public List<String> getAudioIdList() {
        return audioIdList;
    }
    public void setAudioIdList(List<String> audioIdList) {
        this.audioIdList = audioIdList;
    }


    public void addAudio(Audio audio){
        if (audioIdList == null){
            audioIdList = new ArrayList<>();
        }
        audioIdList.add(audio.getUniqueID());
    }

    /**
     *  Updates this User object with database's version.
     *
     *  Throws an exception if username is already taken or
     *  does not follow criteria
     */
    public void update() throws UsernameException {
        //TODO: Database code
    }

    public static class Builder {

        private User user;

        public Builder(){
            user = new User();
        }

        public Builder withUsername(String username){
            user.username = username;
            return this;
        }

        public User build() throws UsernameException {
            if (user.username == null){
                throw new IllegalStateException("User cannot be built without a username");
            }
            if (user.username.length() > 12){
                throw new UsernameException("Username too long.");
            }
            if (user.username.length() > 12 || user.username.matches("([^a-z0-9 ])")){
                throw new UsernameException("Illegal username");
            }
            user.registerTime = System.currentTimeMillis();
            user.userID = "user_" + UUID.nameUUIDFromBytes(user.username.getBytes()).toString().replace("-","");
            user.audioIdList = new ArrayList<>();
            return user;
        }
    }

}
