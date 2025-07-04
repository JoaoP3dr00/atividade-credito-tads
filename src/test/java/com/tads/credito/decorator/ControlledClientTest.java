package com.tads.credito.decorator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControlledClientTest {

    @Mock
    private ScoreClient wrappedClient;

    @InjectMocks
    private ControlledClient controlledClient;

    private final String testCpf = "12345678901";
    private final int expectedScore = 750;

    @Test
    void getScore_shouldCallWrappedClientOnce_whenSingleRequest() {
        // Arrange
        when(wrappedClient.getScore(testCpf)).thenReturn(expectedScore);

        // Act
        int result = controlledClient.getScore(testCpf);

        // Assert
        assertEquals(expectedScore, result);
        verify(wrappedClient, times(1)).getScore(testCpf);
    }

    @Test
    void getScore_shouldEnforceRateLimit_whenMultipleRequests() {
        // Arrange
        when(wrappedClient.getScore(testCpf)).thenReturn(expectedScore);

        // Act - First call
        long startTime = System.currentTimeMillis();
        int result1 = controlledClient.getScore(testCpf);

        // Second call immediately after
        int result2 = controlledClient.getScore(testCpf);
        long elapsedTime = System.currentTimeMillis() - startTime;

        // Assert
        assertEquals(expectedScore, result1);
        assertEquals(expectedScore, result2);
        verify(wrappedClient, times(2)).getScore(testCpf);
        assertTrue(elapsedTime >= 1000, "Requests should be at least 1 second apart");
    }
}