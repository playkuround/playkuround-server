package com.playkuround.playkuroundserver.learning.beanValidation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Disabled
@DisplayName("Bean Validation 및 lombok NonNull 테스트")
@SpringBootTest(properties = "spring.profiles.active=test")
public class ValidationTest {

    @Autowired
    private EntityManagerFactory emf;

    @Test
    @DisplayName("필드에 직접 애노테이션을 붙이면, 검증은 persist 과정에서 수행된다.")
    void fieldAnnotation() {
        // given
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // expected
        FieldAnnotation fieldAnnotation = new FieldAnnotation("", -1);
        assertThatThrownBy(() -> em.persist(fieldAnnotation))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("생성자 파라미터의 bean validation 애노테이션은 작동하지 않는다.")
    void constructAnnotation() {
        // given
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // when
        ConstructAnnotation constructAnnotation = new ConstructAnnotation("", -1);
        em.persist(constructAnnotation);
        em.getTransaction().commit();

        // when
        List<ConstructAnnotation> resultList =
                em.createQuery("select c from ConstructAnnotation c", ConstructAnnotation.class)
                        .getResultList();
        assertThat(resultList).hasSize(1)
                .extracting("name", "age")
                .containsExactly(tuple("", -1));
    }

    @Test
    @DisplayName("생성자 파라미터에 @NonNull(lombok)을 붙이면, 검증은 생성자 호출 시점에 수행된다.")
    void lombokNotNullInConstructor() {
        assertThatThrownBy(() -> {
            new LombokNotNullInConstructor(null, -1);
        }).isInstanceOf(NullPointerException.class);
    }
}
