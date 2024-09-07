package com.showcase.application.models.configurations;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Document {
    private byte[] file;
    private String name;
}
