package com.traverse.data;


import com.traverse.utils.TimeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.UUID;


public class Audio {

    private static final Log logger = LogFactory.getLog(Audio.class);

    private String name; //Name for audio clip. Does not need to be unique
    private String id; //Auto generated when pushed.
    private String ownerID; //Owner of this audio clip
    private String description;
    private Long uploadTime;
    private Long uploadDate;

    public static final String
            DB_IDENTIFIER_AUDIO_NAME = "audio_name",
            DB_IDENTIFIER_AUDIO_ID = "audio_id",
            DB_IDENTIFIER_UPLOAD_TIME = "audio_upload_time",
            DB_IDENTIFIER_UPLOAD_DATE = "audio_upload_date",
            DB_IDENTIFIER_AUDIO_OWNER_ID = "audio_owner_id",
            DB_IDENTIFIER_AUDIO_DESCRIPTION = "audio_description";

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerID() {
        return ownerID;
    }
    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUploadDate() {
        return uploadDate;
    }
    public void setUploadDate(long uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Long getUploadTime() {
        return uploadTime;
    }
    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String toJson(){
        return new JSONObject()
                .put(DB_IDENTIFIER_AUDIO_NAME, name)
                .put(DB_IDENTIFIER_AUDIO_ID, id)
                .put(DB_IDENTIFIER_UPLOAD_TIME, uploadTime)
                .put(DB_IDENTIFIER_UPLOAD_DATE, uploadDate)
                .put(DB_IDENTIFIER_AUDIO_OWNER_ID, ownerID)
                .put(DB_IDENTIFIER_AUDIO_DESCRIPTION, description)
                .toString();
    }

    public static Audio fromJSON(String jsonString){
        if (jsonString == null){
            return null;
        }
        JSONObject jsonObject = new JSONObject(jsonString);
        return new Builder()
                .withName(jsonObject.getString(DB_IDENTIFIER_AUDIO_NAME))
                .withID(jsonObject.getString(DB_IDENTIFIER_AUDIO_ID))
                .withUploadTime(jsonObject.getLong(DB_IDENTIFIER_UPLOAD_TIME))
                .withUploadDate(jsonObject.getLong(DB_IDENTIFIER_UPLOAD_DATE))
                .withOwnerID(jsonObject.getString(DB_IDENTIFIER_AUDIO_OWNER_ID))
                .withDescription(jsonObject.getString(DB_IDENTIFIER_AUDIO_DESCRIPTION))
                .build();
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

        public Builder withID(String id){
            audio.id = id;
            return this;
        }

        public Builder withUploadTime(long uploadTime){
            audio.uploadTime = uploadTime;
            return this;
        }

        public Builder withUploadDate(long uploadDate){
            audio.uploadDate = uploadDate;
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
            audio.id = UUID.randomUUID().toString().replace("-","");

            long currentTime = System.currentTimeMillis();

            audio.uploadTime = currentTime % TimeUtils.MILLIS_IN_DAY;
            audio.uploadDate = currentTime - audio.uploadTime;
            return audio;
        }

    }

}
