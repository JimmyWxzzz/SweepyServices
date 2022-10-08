package com.sweepy.controller;

import com.sweepy.rateLimit.RateLimit;
import com.sweepy.urlService.UrlService;
import com.sweepy.userService.UserService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
public class Controller {

    private UrlService urlService;

    private UserService userService;

    public Controller(UrlService urlService, UserService userService) {
        this.urlService = urlService;
        this.userService = userService;
    }

    @RequestMapping("/")
    public String home() {
        return "this is the home page";
    }
    @PostMapping("/longToShort")
    public String longtoShort(@Param("method") String method, @Param("url") String url, HttpServletResponse response) {
        return  urlService.longToShort(url, method);
    }

    @RateLimit(permitsPerSecond = 0.2)
    @RequestMapping("/goto")
    public String shortToLong(@Param("url") String url, HttpServletResponse response) throws IOException {

        return urlService.shortToLong(url, response);
    }

    @PostMapping("/signup")
    public String signUp(@RequestBody Map<String, String> userDetail) {


        return userService.register(userDetail.get("username"), userDetail.get("password"));
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> userDetail, HttpServletResponse response) {
        response.getHeaders("Authorization");
        if (userService.login(userDetail.get("username"), userDetail.get("password"))) {
            return response.getHeader("Authorization");
        }
        else {
            return "Wrong login credentials";
        }
    }

    @PostMapping("/test")
    public String test() {
        return "Keycloak Logged in";
    }



}
