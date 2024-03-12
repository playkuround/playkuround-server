package com.playkuround.playkuroundserver.domain.auth.token.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GrantType {

    BEARER("Bearer");

    private final String type;
}
