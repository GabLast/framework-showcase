package com.showcase.application.simplerestclient.models.dto.request;

public interface BaseRequest<T extends BaseRequestBody, B extends BaseHeader, V extends String> {
    public T getBody();
    public B getHeaders();
    public V getBearer();
}
