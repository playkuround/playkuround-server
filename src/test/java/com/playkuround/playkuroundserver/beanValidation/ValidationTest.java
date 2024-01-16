package com.playkuround.playkuroundserver.beanValidation;

import com.playkuround.playkuroundserver.beanValidation.code.UserConstruct;
import com.playkuround.playkuroundserver.beanValidation.code.UserConstruct2;
import com.playkuround.playkuroundserver.beanValidation.code.UserField;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class ValidationTest {

    @Autowired
    private EntityManagerFactory emf;

    @Test
    @DisplayName("필드에 직접 어노테이션을 붙이면, 검증은 persist 과정에서 수행된다.")
    void testField() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        UserField userField = new UserField("", -1);

        assertThatThrownBy(() -> {
            em.persist(userField);
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("validate 메서드를 별도로 호출하지 않는한, 생성자 인수의 애노테이션은 작동하지 않는다.")
    void testConstruct() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        UserConstruct userField = new UserConstruct("", -1);

        em.persist(userField);
        em.getTransaction().commit();
    }

    @Test
    @DisplayName("생성자 인수에 @NonNull(lombok)을 붙이면, 검증은 생성자 호출 시점에 수행된다.")
    void testConstructNonnull() {

        assertThatThrownBy(() -> {
            new UserConstruct2(null, -1);
        }).isInstanceOf(NullPointerException.class);


        assertThatThrownBy(() -> {
            UserConstruct2.builder()
                    .name(null)
                    .age(-1)
                    .build();
        }).isInstanceOf(NullPointerException.class);
    }
}
