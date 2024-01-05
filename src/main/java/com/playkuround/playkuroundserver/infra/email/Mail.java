package com.playkuround.playkuroundserver.infra.email;

public record Mail(
        String target,
        String title,
        String content,
        String subtype,
        String encoding,
        String fromPersonal,
        String fromAddress
) {
    public Mail(String target, String title, String content) {
        this(target, title, content, "HTML", "UTF-8",
                "플레이쿠라운드", "playkuround@gmail.com");
    }
}
