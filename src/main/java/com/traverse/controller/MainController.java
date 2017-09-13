package com.traverse.controller;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller("/")
public class MainController {

    private static final Log logger = LogFactory.getLog(MainController.class);

    @RequestMapping("/")
    public String index() {
        return "index";
    }


}
