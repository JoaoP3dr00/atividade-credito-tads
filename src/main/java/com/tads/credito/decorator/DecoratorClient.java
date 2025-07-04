package com.tads.credito.decorator;

public abstract class DecoratorClient implements ScoreClient {
    protected final ScoreClient wrapped;

    protected DecoratorClient(ScoreClient wrapped) {
        this.wrapped = wrapped;
    }
}