package com.tads.credito.decorator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachedClientTest {

    @Mock
    private ScoreClient wrappedClient;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private CachedClient cachedClient;

    private final String testCpf = "123.456.789-09";
    private final int expectedScore = 450;

    @BeforeEach
    void setUp() {
        when(cacheManager.getCache("scores")).thenReturn(cache);
        cachedClient = new CachedClient(wrappedClient, cacheManager);
    }

    @Test
    void shouldCallWrappedClientAndStoreInCacheWhenNotCached() {
        // Arrange
        when(cache.get(testCpf)).thenReturn(null);
        when(wrappedClient.getScore(testCpf)).thenReturn(expectedScore);

        // Act
        int result = cachedClient.getScore(testCpf);

        // Assert
        assertEquals(expectedScore, result);
        verify(wrappedClient).getScore(testCpf);
        verify(cache).put(testCpf, expectedScore);
    }

    @Test
    void shouldReturnCachedScoreAndNotCallWrappedClient() {
        // Arrange
        Cache.ValueWrapper wrapper = mock(Cache.ValueWrapper.class);
        when(wrapper.get()).thenReturn(expectedScore);
        when(cache.get(testCpf)).thenReturn(wrapper);

        // Act
        int result = cachedClient.getScore(testCpf);

        // Assert
        assertEquals(expectedScore, result);
        verify(wrappedClient, never()).getScore(any());
        verify(cache, never()).put(any(), any());
    }

    @Test
    void getScore_shouldCallWrappedClientForDifferentCpfs() {
        // Arrange
        String anotherCpf = "98765432100";
        when(wrappedClient.getScore(testCpf)).thenReturn(750);
        when(wrappedClient.getScore(anotherCpf)).thenReturn(800);

        // Act
        int result1 = cachedClient.getScore(testCpf);
        int result2 = cachedClient.getScore(anotherCpf);

        // Assert
        assertEquals(750, result1);
        assertEquals(800, result2);
        verify(wrappedClient).getScore(testCpf);
        verify(wrappedClient).getScore(anotherCpf);
    }
}