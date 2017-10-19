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

    @RequestMapping("/")
    public String index() {
        return "index";
    }

}

