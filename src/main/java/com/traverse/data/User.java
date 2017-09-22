package com.traverse.data;

import com.traverse.data.structures.Gender;
import com.traverse.exceptions.UsernameException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User object
 *
 * Holds data pertaining to the user profile.
 *
 */
public class User {

    private static final Log logger = LogFactory.getLog(User.class);

    private String username; //Unique identifier
    private Gender gender;

    private User() {} //Do not instantiate

    public String getUsername() {
        return username;
    }

    public Gender getGender() {
        return gender;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
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

    /**
     * Grabs user with the username from database
     * @param username
     * @return null if not found.
     */
    public static User getUser(String username){
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (username != null ? !username.equals(user.username) : user.username != null) return false;
        return gender == user.gender;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        return result;
    }

    private static class Builder {

        private User user;

        public Builder(){
            user = new User();
        }

        public Builder setUsername(String username){
            user.username = username;
            return this;
        }

        public Builder setGender(Gender gender){
            user.gender = gender;
            return this;
        }

        public User build(){
            if (user.username == null){
                throw new IllegalStateException("User cannot be built without a username");
            }
            return user;
        }
    }

}
