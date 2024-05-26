package com.playkuround.playkuroundserver.reflection;

import com.playkuround.playkuroundserver.reflection.code.Domain;
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
    void test_1() throws Exception {
        Constructor<Domain> declaredConstructor = Domain.class.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        Domain domain = declaredConstructor.newInstance();

        assertThat(domain.getName()).isNull();
        assertThat(domain.getAge()).isZero();
    }

    @Test
    @DisplayName("인자 타입을 이용해 생성자를 선택할 수 있다.")
    void test_2() throws Exception {
        Constructor<Domain> declaredConstructor = Domain.class.getDeclaredConstructor(String.class);
        declaredConstructor.setAccessible(true);
        Domain domain = declaredConstructor.newInstance("name");

        assertThat(domain.getName()).isEqualTo("name");
        assertThat(domain.getAge()).isZero();
    }

    @Test
    @DisplayName("setAccessible을 true로 바꾸지 않으면 IllegalAccessException이 발생한다.")
    void test_3() throws Exception {
        Constructor<Domain> declaredConstructor = Domain.class.getDeclaredConstructor(String.class);

        assertThatThrownBy(() -> {
            declaredConstructor.newInstance("name");
        }).isInstanceOf(IllegalAccessException.class);
    }

    @Test
    @DisplayName("필드 정보 바꾸기")
    void test_4() throws Exception {
        Class<Domain> domainClass = Domain.class;
        Constructor<Domain> declaredConstructor = domainClass.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        Domain landmark = declaredConstructor.newInstance();

        assertThat(landmark.getName()).isNull();

        Field targetField = domainClass.getDeclaredField("name");
        targetField.setAccessible(true);
        targetField.set(landmark, "test");

        assertThat(landmark.getName()).isEqualTo("test");
    }

    @Test
    @DisplayName("private 필드를 바꿀 땐 setAccessible을 true로 바꾸지 않으면 IllegalAccessException이 발생한다.")
    void test_5() throws Exception {
        Class<Domain> domainClass = Domain.class;
        Constructor<Domain> declaredConstructor = domainClass.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        Domain landmark = declaredConstructor.newInstance();
        Field targetField = domainClass.getDeclaredField("name");
        assertThatThrownBy(() -> {
            targetField.set(landmark, "test");
        }).isInstanceOf(IllegalAccessException.class);
    }

    @Test
    @DisplayName("선언되지 않는 필드 이름이면 NoSuchFieldException이 발생한다.")
    void test_6() throws Exception {
        Class<Domain> domainClass = Domain.class;
        Constructor<Domain> declaredConstructor = domainClass.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        Domain landmark = declaredConstructor.newInstance();

        assertThat(landmark.getName()).isNull();

        assertThatThrownBy(() -> {
            domainClass.getDeclaredField("notDeclaredField");
        }).isInstanceOf(NoSuchFieldException.class);
    }
}
