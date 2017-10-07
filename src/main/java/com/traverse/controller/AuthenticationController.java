package com.traverse.controller;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping(value = {"/oauth"})
public class AuthenticationController {

    @RequestMapping(value = "/facebook/callback", method = RequestMethod.POST)
    public void callback(HttpEntity<String> httpEntity){
        String json = httpEntity.getBody();
        System.out.println(json);
    }


}
