package com.showcase.application.simplerestclient.dto.request;

import com.showcase.application.simplerestclient.dto.JsonBase;

public interface BaseRequestGet<T, B, V> extends JsonBase {
    public T getParams();
    public B getHeaders();
    public V getBearer();
}
