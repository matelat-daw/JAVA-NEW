package com.futureprograms.prueba.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/all")
    public ResponseEntity<?> allAccess() {
        return ResponseEntity.ok("Public Content");
    }

    @GetMapping("/user")
    public ResponseEntity<?> userAccess() {
        return ResponseEntity.ok("User Content");
    }

    @GetMapping("/admin")
    public ResponseEntity<?> adminAccess() {
        return ResponseEntity.ok("Admin Content");
    }
}