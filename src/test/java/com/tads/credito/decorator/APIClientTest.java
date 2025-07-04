package com.tads.credito.decorator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class APIClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private APIClient apiClient;

    private final String testCpf = "12345678901";
    private final String apiUrl = "https://score.hsborges.dev/api/score?cpf=" + testCpf;

    @Test
    void getScore_shouldReturnScore_whenApiReturnsSuccess() {
        // Arrange
        int expectedScore = 750;
        when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.GET), any(), eq(Integer.class)))
                .thenReturn(new ResponseEntity<>(expectedScore, HttpStatus.OK));

        // Act
        int result = apiClient.getScore(testCpf);

        // Assert
        assertEquals(expectedScore, result);
        verify(restTemplate).exchange(eq(apiUrl), eq(HttpMethod.GET), any(), eq(Integer.class));
    }

    @Test
    void getScore_shouldThrowException_whenApiReturns429() {
        // Arrange
        when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.GET), any(), eq(Integer.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));

        // Act & Assert
        assertThrows(HttpClientErrorException.class, () -> apiClient.getScore(testCpf));
    }

    @Test
    void getScore_shouldThrowRuntimeException_whenGenericErrorOccurs() {
        // Arrange
        when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.GET), any(), eq(Integer.class)))
                .thenThrow(new RuntimeException("Connection error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> apiClient.getScore(testCpf));
    }
}