package com.playkuround.playkuroundserver;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Disabled
public class JavaTest {

    @Test
    @DisplayName("Wrapper가 null일 때는 비교 연산자를 사용할 수 없다.(NullPointerException)")
    void wrapperTest() {
        Double value = null;

        assertThatThrownBy(() -> {
            boolean result = value <= 90.0;
        }).isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> {
            boolean result = value == 90.0;
        }).isInstanceOf(NullPointerException.class);
    }
}
