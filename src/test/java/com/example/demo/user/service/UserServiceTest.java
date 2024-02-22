package com.example.demo.user.service;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserUpdate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
    @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private JavaMailSender javaMailSender;

    @DisplayName("이메일로 유저 조회 시 ACTIVE 상태의 유저가 조회된다.")
    @Test
    void getByEmail_Active() {
        /// given
        String email = "member1@test.com";

        // when
        User result = userService.getByEmail(email);

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

    @DisplayName("UserCreateDto로 유저 저장이 정상적으로 된다.")
    @Test
    void create() {
        // given
        UserCreate createDto = UserCreate.builder()
                .email("member3@test.com")
                .nickname("member3")
                .address("Seoul")
                .build();

        BDDMockito.doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // when
        User save = userService.create(createDto);

        // then
        assertThat(save).isNotNull();
        assertThat(save.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(save.getEmail()).isEqualTo(createDto.getEmail());
    }

    @DisplayName("UserUpdateDto 로 유저 데이터가 변경된다.")
    @Test
    void update() {
        // given
        UserUpdate updateDto = UserUpdate.builder()
                .nickname("member-update")
                .address("Incheon")
                .build();

        // when
        User userEntity = userService.update(1, updateDto);

        // then
        assertThat(userEntity.getNickname()).isEqualTo(updateDto.getNickname());
        assertThat(userEntity.getAddress()).isEqualTo(updateDto.getAddress());
    }

    @DisplayName("로그인 시 마지막 로그인 시간이 변경된다.")
    @Test
    void login() {
        // given
        // when
        userService.login(1);
        // then
        // TODO 로그인 시간 확인 방법이 없음
    }

    @DisplayName("PENDING 상태 사용자는 인증 코드로 ACTIVE로 상태값이 변경된다.")
    @Test
    void verifyEmail() {
        // given
        String code = "aaaa-aaaa-aaaa-aaaa";

        // when
        userService.verifyEmail(2, code);

        // then
        User findUser = userService.getById(2);
        assertThat(findUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }


    @DisplayName("PENDING 상태 사용자는 잘못된 인증 코드를 받으면 예외를 발생시킨다.")
    @Test
    void verifyEmail_incorrect_code() {
        // given
        String code = "aaaa-aaaa-aaaa-aaaa1";
        // when
        // then
        assertThatThrownBy(
                () -> userService.verifyEmail(2, code))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}
