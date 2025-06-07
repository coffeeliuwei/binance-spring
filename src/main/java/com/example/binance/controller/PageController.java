package com.example.binance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面控制器
 */
@Controller
public class PageController {

    /**
     * 首页
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 15分钟统计页面
     */
    @GetMapping("/15min")
    public String fifteenMinStats() {
        return "15min";
    }
}
