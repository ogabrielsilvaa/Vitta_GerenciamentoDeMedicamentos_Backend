package com.vitta.vittaBackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class WebController {

    @GetMapping("/download/app")
    public ResponseEntity<Void> downloadApp() {
        String linkDoGithub = "https://github.com/ogabrielsilvaa/Vitta_GerenciamentoDeMedicamentos_Backend/releases/download/v1.0/app-release.apk";

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(linkDoGithub))
                .build();
    }
}