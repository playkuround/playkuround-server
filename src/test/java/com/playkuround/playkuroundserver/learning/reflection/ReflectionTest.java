package com.playkuround.playkuroundserver.learning.reflection;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Disabled
public class ReflectionTest {

    @Test
    @DisplayName("리플렉션을 이용하면 private 생성자를 호출할 수 있다.")
    void privateConstructor1() throws Exception {
        Constructor<Domain> declaredConstructor = Domain.class.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        Domain domain = declaredConstructor.newInstance();

        assertThat(domain.getAge()).isZero();
        assertThat(domain.getName()).isNull();
    }

    @Test
    @DisplayName("인자 타입을 이용해 생성자를 선택할 수 있다.")
    void privateConstructor2() throws Exception {
        Constructor<Domain> declaredConstructor = Domain.class.getDeclaredConstructor(String.class);
        declaredConstructor.setAccessible(true);

        String name = "name";
        Domain domain = declaredConstructor.newInstance(name);

        assertThat(domain.getAge()).isZero();
        assertThat(domain.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("setAccessible을 true로 바꾸지 않으면 IllegalAccessException이 발생한다.")
    void privateConstructor3() throws Exception {
        Constructor<Domain> declaredConstructor = Domain.class.getDeclaredConstructor(String.class);

        assertThatThrownBy(() -> declaredConstructor.newInstance("name"))
                .isInstanceOf(IllegalAccessException.class);
    }

    @Test
    @DisplayName("필드 정보 바꾸기")
    void changeField() throws Exception {
        Domain domain = new Domain(null, 0);
        assertThat(domain.getName()).isNull();

        Field targetField = Domain.class.getDeclaredField("name");
        targetField.setAccessible(true);

        String name = "test";
        targetField.set(domain, name);

        assertThat(domain.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("private 필드를 바꿀 땐 setAccessible을 true로 바꾸지 않으면 IllegalAccessException이 발생한다.")
    void changeField2() throws Exception {
        Domain domain = new Domain(null, 0);
        Field targetField = Domain.class.getDeclaredField("name");

        assertThatThrownBy(() -> {
            targetField.set(domain, "test");
        }).isInstanceOf(IllegalAccessException.class);
    }

    @Test
    @DisplayName("선언되지 않는 필드 이름이면 NoSuchFieldException이 발생한다.")
    void changeField3() {
        assertThatThrownBy(() -> {
            Domain.class.getDeclaredField("notDeclaredField");
        }).isInstanceOf(NoSuchFieldException.class);
    }

}
