package com.traverse.data;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.traverse.exceptions.UsernameException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User object
 *
 * Holds data pertaining to the user profile.
 *
 */
public class User {

    private static final Log logger = LogFactory.getLog(User.class);

    public static final String
            DB_IDENTIFIER_USER_ID = "user_id",
            DB_IDENTIFIER_REGISTER_TIME = "register_time",
            DB_IDENTIFIER_AUDIO_LIST= "audio_list",
            DB_IDENTIFIER_USERNAME = "username",
            DB_IDENTIFIER_SOCIAL_MEDIA_ID = "social_media_id";


    private String username; //Unique identifier
    private String userID; //Unique identifier key
    private Long registerTime;
    private List<String> audioIdList;
    private String socialMediaID;

    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public Long getRegisterTime() {
        return registerTime;
    }
    public void setRegisterTime(Long registerTime) {
        this.registerTime = registerTime;
    }

    public List<String> getAudioIdList() {
        return audioIdList;
    }
    public void setAudioIdList(List<String> audioIdList) {
        this.audioIdList = audioIdList;
    }

    public String getSocialMediaID() {
        return socialMediaID;
    }
    public void setSocialMediaID(String socialMediaID) {
        this.socialMediaID = socialMediaID;
    }

    public void addAudio(Audio audio){
        if (audioIdList == null){
            audioIdList = new ArrayList<>();
        }
        audioIdList.add(audio.getId());
    }

    public String toJson(){
        return new JSONObject()
                .put(DB_IDENTIFIER_USER_ID, userID)
                .put(DB_IDENTIFIER_REGISTER_TIME, registerTime)
                .put(DB_IDENTIFIER_AUDIO_LIST, audioIdList)
                .put(DB_IDENTIFIER_USERNAME, username)
                .put(DB_IDENTIFIER_SOCIAL_MEDIA_ID, socialMediaID)
                .toString();
    }

    public static User fromJSON(String jsonString){
        if (jsonString == null){
            return null;
        }
        JSONObject jsonObject = new JSONObject(jsonString);
        User.Builder builder = new Builder()
                .withUserID(jsonObject.getString(DB_IDENTIFIER_USER_ID))
                .withRegisterTime(jsonObject.getLong(DB_IDENTIFIER_REGISTER_TIME))
                .withAudioList(jsonObject.getJSONArray(DB_IDENTIFIER_AUDIO_LIST).toList().stream().map(String.class::cast).collect(Collectors.toList()));


        if (jsonObject.has(DB_IDENTIFIER_USERNAME)){
            builder.withUsername(jsonObject.getString(DB_IDENTIFIER_USERNAME));
        }

        if (jsonObject.has(DB_IDENTIFIER_SOCIAL_MEDIA_ID)){
            builder.withSocialMediaID(jsonObject.getString(DB_IDENTIFIER_SOCIAL_MEDIA_ID));
        }
        return builder.build();
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
            user.registerTime = 0L;
        }

        public Builder withUsername(String username){
            user.username = username;
            return this;
        }

        public Builder withUserID(String userID){
            user.userID = userID;
            return this;
        }

        public Builder withRegisterTime(long registerTime){
            user.registerTime = registerTime;
            return this;
        }

        public Builder withAudioList(List<String> audioIdList){
            user.audioIdList = audioIdList;
            return this;
        }

        public Builder withSocialMediaID(String socialMediaID){
            user.socialMediaID = socialMediaID;
            return this;
        }

        public User build(){
            if (user.registerTime == 0L) {
                user.registerTime = System.currentTimeMillis();
            }
            user.userID = "traverse_user_" + user.socialMediaID;
            if (user.audioIdList == null) {
                user.audioIdList = new ArrayList<>();
            }
            return user;
        }
    }

}
