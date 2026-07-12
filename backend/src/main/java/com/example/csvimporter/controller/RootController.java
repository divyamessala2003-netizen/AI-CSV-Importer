package com.example.csvimporter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public String index() {
        return "AI CSV Importer Backend API is running successfully. Please access the application via the Frontend URL.";
    }
}
