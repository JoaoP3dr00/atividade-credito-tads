package com.tads.credito.controller;

import com.tads.credito.decorator.ScoreClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreControllerTest {

    @Mock
    private ScoreClient scoreClient;

    @InjectMocks
    private ScoreController scoreController;

    private final String testCpf = "12345678901";
    private final int expectedScore = 750;

    @Test
    void getScore_shouldReturnScoreFromClient() {
        // Arrange
        when(scoreClient.getScore(testCpf)).thenReturn(expectedScore);

        // Act
        int response = scoreController.getScore(testCpf);

        // Assert
        assertEquals(expectedScore, response);
        verify(scoreClient).getScore(testCpf);
    }

//    @Test
//    void getScore_shouldReturn500_whenClientThrowsException() {
//        // Arrange
//        when(scoreClient.getScore(testCpf)).thenThrow(new RuntimeException("API error"));
//
//        // Act
//        int response = scoreController.getScore(testCpf);
//
//        // Assert
//        assertEquals(500, response);
//    }
}