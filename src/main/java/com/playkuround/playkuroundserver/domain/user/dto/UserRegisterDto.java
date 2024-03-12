package com.playkuround.playkuroundserver.domain.user.dto;

import com.playkuround.playkuroundserver.domain.user.domain.Major;

public record UserRegisterDto(String email, String nickname, Major major) {
}
