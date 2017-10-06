package com.traverse.controller;


import com.traverse.data.Audio;
import com.traverse.data.User;
import com.traverse.data.cloud.AudioDatabase;
import com.traverse.data.cloud.UserDatabase;
import com.traverse.exceptions.UserDoesNotExistException;
import com.traverse.exceptions.UsernameException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller("/")
public class MainController {

    private static final Log logger = LogFactory.getLog(MainController.class);

    @Autowired
    private UserDatabase userDatabase;

    @Autowired
    private AudioDatabase audioDatabase;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/testusage")
    public @ResponseBody String create() throws UserDoesNotExistException, UsernameException {
        User user = new User.Builder()
                .withUsername("mah_username")
                .build();

        Audio audio = new Audio.Builder()
                .withName("Sick Song 1")
                .withDescription("This is a sick song i put together on notepad.")
                .withOwnerID(user.getUserID())
                .build();

        user.addAudio(audio);
        audio.setOwnerID(user.getUserID());

        audioDatabase.update(audio); //Create audio
        userDatabase.update(user); //Create user

        System.out.println(audioDatabase.getAudio(audio.getUniqueID()));
        System.out.println(userDatabase.getUser(user.getUsername()));

        return "We made it to end lmao";
    }


}

