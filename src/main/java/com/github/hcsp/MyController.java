package com.github.hcsp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController {
    @GetMapping("/pdf")
    @ResponseBody
    public String generatePdfForDownloading() {
        // 假装在做一些事情
        byte[] a = new byte[20 * 1024 * 1024];
        for (int i = 0; i < 10; i++) {
            // 申请临时的内存空间，假装在做一些事情
            byte[] tmp = new byte[10 * 1024 * 1024];
        }
        return "OK";
    }
}
