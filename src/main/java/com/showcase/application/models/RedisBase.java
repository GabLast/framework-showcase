package com.showcase.application.models;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RedisBase implements Serializable {
    String id;
}