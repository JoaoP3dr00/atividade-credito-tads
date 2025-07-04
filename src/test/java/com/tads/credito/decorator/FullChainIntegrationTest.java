package com.tads.credito.decorator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FullChainIntegrationTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Cache cache;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private ScoreClient wrappedClient;

    @InjectMocks
    private APIClient apiClient;

    @InjectMocks
    private CachedClient cachedClient;

    private final String testCpf = "123.456.789-09";
    private final String apiUrl = "https://score.hsborges.dev/api/score?cpf=" + testCpf;
    private final int expectedScore = 450;

    @BeforeEach
    void setUp() {
        when(cacheManager.getCache("scores")).thenReturn(cache);
        cachedClient = new CachedClient(wrappedClient, cacheManager);
    }

    @Test
    void fullChain_shouldWorkWithCacheAndRateLimit() {
        // Arrange - Configure the full chain
        ControlledClient controlledClient = new ControlledClient(apiClient);

        // Mock API response
        when(wrappedClient.getScore(testCpf)).thenReturn(expectedScore);
        when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.GET), any(), eq(Integer.class)))
                .thenReturn(new ResponseEntity<>(expectedScore, HttpStatus.OK));

        // Act - First call (should call API)
        long startTime = System.currentTimeMillis();
        int result1 = apiClient.getScore(testCpf);

        // Second call (should use cache)
        int result2 = cachedClient.getScore(testCpf);

        // Third call with different CPF (should call API with rate limit)
        String anotherCpf = "98765432100";
        when(restTemplate.exchange(
                eq("https://score.hsborges.dev/api/score?cpf=" + anotherCpf),
                eq(HttpMethod.GET), any(), eq(Integer.class)))
                .thenReturn(new ResponseEntity<>(800, HttpStatus.OK));

        int result3 = controlledClient.getScore(anotherCpf);
        long elapsedTime = System.currentTimeMillis() - startTime;

        // Assert
        assertEquals(expectedScore, result1);
        assertEquals(expectedScore, result2);
        assertEquals(800, result3);

        // Verify API was called only twice (once for each CPF)
        verify(restTemplate, times(1))
                .exchange(eq(apiUrl), eq(HttpMethod.GET), any(), eq(Integer.class));
        verify(restTemplate, times(1))
                .exchange(eq("https://score.hsborges.dev/api/score?cpf=" + anotherCpf),
                        eq(HttpMethod.GET), any(), eq(Integer.class));

        // Verify rate limiting - at least 1 second between API calls
        assertTrue(elapsedTime >= 1, "Rate limit not enforced");
    }

    @Test
    void shouldWorkWithMultipleCpfs() {
        List<String> cpfList = CpfGenerator.generateCpfList(5);
        for (String cpf : cpfList) {
            System.out.println("Gerado CPF de teste: " + cpf);
            when(restTemplate.exchange(
                    eq("https://score.hsborges.dev/api/score?cpf=" + cpf),
                    eq(HttpMethod.GET), any(), eq(Integer.class)))
                    .thenReturn(new ResponseEntity<>(700, HttpStatus.OK));

            int score = apiClient.getScore(cpf);
            assertEquals(700, score);
        }
    }
}