package com.tads.credito.decorator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ControlledClient extends DecoratorClient {
    private long lastRequestTime = 0;

    public ControlledClient(@Qualifier("scoreClient") ScoreClient wrapped) {
        super(wrapped);
    }

    @Override
    public synchronized int getScore(String cpf) {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastRequestTime;

        if (elapsed < 1000) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000 - elapsed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        lastRequestTime = System.currentTimeMillis();
        return wrapped.getScore(cpf);
    }
}