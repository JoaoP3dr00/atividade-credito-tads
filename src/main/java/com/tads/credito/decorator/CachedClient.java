package com.tads.credito.decorator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class CachedClient extends DecoratorClient {
    private final Cache cache;

    public CachedClient(@Qualifier("scoreClient") ScoreClient wrapped, CacheManager cacheManager) {
        super(wrapped);
        this.cache = cacheManager.getCache("scores");
    }

    @Override
    public int getScore(String cpf) {
        Cache.ValueWrapper cached = cache.get(cpf);
        if (cached != null) {
            return (Integer) cached.get();
        }

        int score = wrapped.getScore(cpf);
        cache.put(cpf, score);
        return score;
    }
}
