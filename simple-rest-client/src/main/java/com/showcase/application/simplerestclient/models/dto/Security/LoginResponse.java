package com.showcase.application.simplerestclient.models.dto.Security;

import com.showcase.application.simplerestclient.models.dto.BaseClient;
import com.showcase.application.simplerestclient.models.dto.RequestFrame;

public class LoginResponse extends BaseClient {
    private RequestFrame requestFrame;
    private UserRest userRest;
    private String jwt;
}
