package com.traverse.data;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;
import java.util.UUID;


@DynamoDBTable(tableName="traverse_audio")
public class Audio {

    private static final Log logger = LogFactory.getLog(Audio.class);

    private String name; //Name for audio clip. Does not need to be unique
    private String uniqueID; //Auto generated when pushed.
    private String ownerID; //Owner of this audio clip
    private String description;
    private Long uploadTime;

    public static final String
            DB_IDENTIFIER_AUDIO_ID = "audio_id",
            DB_IDENTIFIER_UPLOAD_TIME = "audio_upload_time",
            DB_IDENTIFIER_AUDIO_OWNER_ID = "audio_owner_id",
            DB_IDENTIFIER_AUDIO_DESCRIPTION = "audio_description";


    @DynamoDBAttribute(attributeName="audio_name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBHashKey(attributeName=DB_IDENTIFIER_AUDIO_ID)
    public String getUniqueID() {
        return uniqueID;
    }
    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    @DynamoDBAttribute(attributeName=DB_IDENTIFIER_AUDIO_OWNER_ID)
    public String getOwnerID() {
        return ownerID;
    }
    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    @DynamoDBAttribute(attributeName=DB_IDENTIFIER_UPLOAD_TIME)
    public Long getUploadTime() {
        return uploadTime;
    }
    public void setUploadTime(Long uploadTime) {
        this.uploadTime = uploadTime;
    }

    @DynamoDBAttribute(attributeName=DB_IDENTIFIER_AUDIO_DESCRIPTION)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return URL for this audio clip. Grabs url from server.
     */
    public URL getURL(){
        return null;
    }

    /**
     *  Updates this Audio object with database's version.
     */
    public void update(){
        uniqueID = null;  //TODO: Assign this variable on update
        //TODO: Database code
    }

    /**
     * Grabs audio from database with the unique ID
     * @param uniqueID
     * @return null if not found.
     */
    public static Audio getAudio(String uniqueID){
        return null;
    }


    public static class Builder {

        private Audio audio;

        public Builder(){
            audio = new Audio();
        }

        public Builder withName(String name){
            audio.name = name;
            return this;
        }

        public Builder withOwnerID(String ownerID){
            audio.ownerID = ownerID;
            return this;
        }

        public Builder withDescription(String description){
            audio.description = description;
            return this;
        }

        public Audio build(){
            if (audio.ownerID == null){
                throw new IllegalStateException("Audio cannot have null for ownerID");
            }
            if (audio.name == null){
                throw new IllegalStateException("Audio cannot have null for name");
            }
            if (audio.name.matches("([^a-z0-9 ])")){
                throw new IllegalStateException("Audio name is invalid. Can only contain alphanumeric characters and spaces.");
            }
            if (audio.name.length() > 64){
                throw new IllegalStateException("Audio name cannot exceed 100 characters.");
            }
            audio.uniqueID = UUID.randomUUID().toString().replace("-","");
            audio.uploadTime = System.currentTimeMillis();
            return audio;
        }

    }

}
