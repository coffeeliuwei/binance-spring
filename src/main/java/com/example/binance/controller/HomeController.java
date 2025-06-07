package com.example.binance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器
 */
@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "index";
    }
}