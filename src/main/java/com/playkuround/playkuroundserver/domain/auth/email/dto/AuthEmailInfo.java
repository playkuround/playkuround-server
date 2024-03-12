package com.playkuround.playkuroundserver.domain.auth.email.dto;

import java.time.LocalDateTime;

public record AuthEmailInfo(LocalDateTime expiredAt, long sendingCount) {
}
