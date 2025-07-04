package com.tads.credito.controller;

import com.tads.credito.decorator.ScoreClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/score")
public class ScoreController {
    private final ScoreClient scoreClient;

    public ScoreController(ScoreClient scoreClient) {
        this.scoreClient = scoreClient;
    }

    @GetMapping("/{cpf}")
    public int getScore(@PathVariable String cpf) {
        return scoreClient.getScore(cpf);
    }
}