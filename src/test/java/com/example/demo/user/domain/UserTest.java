package com.example.demo.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @DisplayName("User는 UserCreate 객체로 생성할 수 있다.")
    @Test
    void createUser() {
        // given

        // when

        // then
    }

    @DisplayName("User는 UserUpdate 객체로 데이터를 업데이트 할 수 있다.")
    @Test
    void updateUser() {
        // given

        // when

        // then
    }

    @DisplayName("User는 로그인을 할 수 있고, 로그인 시 마지막 로그인 시간이 변경된다.")
    @Test
    void login() {
        // given

        // when

        // then
    }

    @DisplayName("User는 유효한 인증 코드로 계정을 활성화 할 수 있다.")
    @Test
    void certificate() {
        // given

        // when

        // then
    }

    @DisplayName("User는 잘못된 인증 코드로 계정을 활성화 시 예외가 발생한다.")
    @Test
    void certificate_invalid_code() {
        // given

        // when

        // then
    }
}
