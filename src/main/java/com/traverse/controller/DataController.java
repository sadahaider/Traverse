package com.traverse.controller;


import com.traverse.data.Audio;
import com.traverse.data.User;
import com.traverse.data.cloud.AudioDatabase;
import com.traverse.data.cloud.UserDatabase;
import com.traverse.exceptions.UserDoesNotExistException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = {"/data"})
public class DataController {

    @Autowired
    private UserDatabase userDatabase;

    @Autowired
    private AudioDatabase audioDatabase;

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public String getUser(@PathVariable("id") String id, HttpServletResponse httpServletResponse) throws UserDoesNotExistException, IOException {
        String response = userDatabase.getUserJson(id);
        if (response == null){
            httpServletResponse.sendError(HttpStatus.NOT_FOUND.value(), "User does not exist.");
        }
        return response;
    }

    @RequestMapping(value = "/user/{id}/setUsername", method = RequestMethod.GET)
    public void createUser(@RequestParam("username") String username, @PathVariable("id") String id, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        if (userDatabase.doesUserExist(username)){
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "User " + username + " already exists.");
            return;
        }
        User user = userDatabase.getUserByID(id);
        if (user == null){
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "No user with id: " + id);
            return;
        }
        userDatabase.update(user);
        httpServletResponse.sendError(HttpServletResponse.SC_OK, "Success");
    }

    @RequestMapping(value = "/audio/{id}", method = RequestMethod.GET)
    public String getAudio(@PathVariable("id") String id, HttpServletResponse httpServletResponse) throws IOException {
        String response = audioDatabase.getAudioJson(id);
        if (response == null){
            httpServletResponse.sendError(HttpStatus.NOT_FOUND.value(), "Audio does not exist.");
        }
        return response;
    }

    @RequestMapping(value = "/audio/create", method = RequestMethod.POST)
    public String createAudio(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {

        if (httpServletRequest.getAttribute("name") == null){
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "No name attribute");
        }
        if (httpServletRequest.getAttribute("description") == null){
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "No description attribute");
        }
        if (httpServletRequest.getAttribute("ownerID") == null){
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "No ownerID attribute");
        }

        String userID = httpServletRequest.getAttribute("ownerID").toString();

        Audio audio = new Audio.Builder()
                .withName(httpServletRequest.getAttribute("name").toString())
                .withDescription(httpServletRequest.getAttribute("description").toString())
                .withOwnerID(userID).build();

        User user = userDatabase.getUserByID(userID); //Lets check if the user exists.

        if (user == null){
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid userID: No such user");
            return null;
        }

        //Audio checks here.

        user.addAudio(audio);
        audioDatabase.create(audio);    //Create audio
        userDatabase.update(user);      //Update user


        return new JSONObject().put("result", "success").toString();
    }


}
