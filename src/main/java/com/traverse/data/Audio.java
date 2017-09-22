package com.traverse.data;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;


public class Audio {

    private static final Log logger = LogFactory.getLog(Audio.class);

    private String name; //Name for audio clip. Does not need to be unique
    private String uniqueID; //Auto generated when pushed.
    private String ownerName; //Owner of this audio clip

    private Audio(){} //Do not instantiate. User builder instead.

    public String getName() {
        return name;
    }

    /**
     * This method will return null when Audio is initially built.
     *
     * In cases where you require unique ID, please call <code>update()</code>
     * method, which will generate uuid after uploading to database.
     *
     * @return Unique identifier of audio clip
     */
    public String getUniqueID() {
        return uniqueID;
    }

    public String getOwnerName() {
        return ownerName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Audio audio = (Audio) o;

        if (name != null ? !name.equals(audio.name) : audio.name != null) return false;
        if (uniqueID != null ? !uniqueID.equals(audio.uniqueID) : audio.uniqueID != null) return false;
        return ownerName != null ? ownerName.equals(audio.ownerName) : audio.ownerName == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (uniqueID != null ? uniqueID.hashCode() : 0);
        result = 31 * result + (ownerName != null ? ownerName.hashCode() : 0);
        return result;
    }

    public static class Builder {

        private Audio audio;

        public Builder(){
            audio = new Audio();
        }

        public Builder setName(String name){
            audio.name = name;
            return this;
        }

        public Builder setOwnerName(String ownerName){
            audio.ownerName = ownerName;
            return this;
        }

        public Audio build(){
            if (audio.ownerName == null){
                throw new IllegalStateException("Audio cannot have null for ownerName");
            }
            return audio;
        }

    }

}
