package com.playkuround.playkuroundserver.global.util;

import com.vane.badwordfiltering.BadWordFiltering;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BadWordFilterUtils {

    private static final BadWordFiltering badWordFiltering = new BadWordFiltering();

    public static boolean check(String text) {
        return badWordFiltering.check(text);
    }
}
