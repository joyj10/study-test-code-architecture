package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.repository.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
    @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})

class UserServiceTest {

    @Autowired
    private UserService userService;

    @DisplayName("이메일로 유저 조회 시 ACTIVE 상태의 유저가 조회된다.")
    @Test
    void getByEmail_Active() {
        /// given
        String email = "member1@test.com";

        // when
        UserEntity result = userService.getByEmail(email);

        // then
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @DisplayName("이메일로 유저 조회 시 PENDING 상태의 유저는 조회 불가능 하다.")
    @Test
    void getByEmail_Pending() {
        /// given
        String email = "member2@test.com";

        // when
        // then
        assertThatThrownBy(
                () -> userService.getByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class);
    }

}
