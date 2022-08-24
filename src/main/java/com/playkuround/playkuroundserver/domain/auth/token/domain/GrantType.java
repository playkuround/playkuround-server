package com.playkuround.playkuroundserver.domain.auth.token.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GrantType {

    BEARER("Bearer");

    private final String type;

}
