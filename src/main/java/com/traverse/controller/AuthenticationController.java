package com.traverse.controller;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.traverse.data.cloud.AuthDatabase;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
@RequestMapping(value = {"/oauth"})
public class AuthenticationController {

    @Autowired
    private AuthDatabase authDatabase;

    private String appID, appSecret;

    public AuthenticationController(
            @Value("${spring.social.facebook.appId}") String appID,
            @Value("${spring.social.facebook.appSecret}") String appSecret){
        this.appID = appID;
        this.appSecret = appSecret;
    }

    @RequestMapping(value = "/getUser/{token}", method = RequestMethod.GET)
    public String getUser(@PathVariable(value = "token") String token){
        return new JSONObject()
                .put("response", authDatabase.getUserID(token))
                .toString();
    }

    /**
     * Callback function. Pass in facebook accessToken and userID.
     *
     * userID in this case is the social media ID.
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @throws IOException
     */
    @RequestMapping(value = "/facebook", method = RequestMethod.POST)
    public void callback(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        String token = httpServletRequest.getAttribute("accessToken").toString();
        String socialMediaID = httpServletRequest.getAttribute("userID").toString();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://graph.facebook.com/app?access_token=" + token)
                .get()
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "8ee00117-9fc1-a322-1cc9-3a6f86144dd4")
                .build();

        Response response = client.newCall(request).execute();
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(response.body().string());
        } catch (JSONException e){
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid response syntax");
            return;
        }

        if (jsonObject.get("id") == null || !jsonObject.get("id").equals(appID)){
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token does not have access to our app.");
            return;
        }

        authDatabase.set(token, socialMediaID); //Store the login in database for authentication.
        httpServletResponse.addCookie(new Cookie("facebook_token", token));
    }


}
