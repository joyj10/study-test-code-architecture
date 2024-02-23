package com.example.demo.user.domain;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {
    @DisplayName("User는 UserCreate 객체로 생성할 수 있다.")
    @Test
    void createUser() {
        // given
        UserCreate userCreate = UserCreate.builder()
                .email("member1@test.com")
                .nickname("member1")
                .address("Seoul")
                .build();

        // when
        String uuid = "aaaa-aaaa-aaaa-aaaa";
        User user = User.from(userCreate, new TestUuidHolder(uuid));

        // then
        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo(userCreate.getEmail());
        assertThat(user.getNickname()).isEqualTo(userCreate.getNickname());
        assertThat(user.getAddress()).isEqualTo(userCreate.getAddress());
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getCertificationCode()).isEqualTo(uuid);
    }

    @DisplayName("User는 UserUpdate 객체로 데이터를 업데이트 할 수 있다.")
    @Test
    void updateUser() {
        // given
        User user = User.builder()
                .id(1L)
                .email("member1@test.com")
                .nickname("member1")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .build();
        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("member1-k")
                .address("Pangyo")
                .build();

        // when
        user = user.update(userUpdate);

        // then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("member1@test.com");
        assertThat(user.getNickname()).isEqualTo("member1-k");
        assertThat(user.getAddress()).isEqualTo("Pangyo");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getCertificationCode()).isEqualTo("aaaa-aaaa-aaaa-aaaa");
        assertThat(user.getLastLoginAt()).isEqualTo(100L);
    }

    @DisplayName("User는 로그인을 할 수 있고, 로그인 시 마지막 로그인 시간이 변경된다.")
    @Test
    void login() {
        // given
        User user = User.builder()
                .id(1L)
                .email("member1@test.com")
                .nickname("member1")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .build();

        // when
        user = user.login(new TestClockHolder(1678530673958L));

        // then
        assertThat(user.getLastLoginAt()).isEqualTo(1678530673958L);
    }

    @DisplayName("User는 유효한 인증 코드로 계정을 활성화 할 수 있다.")
    @Test
    void certificate() {
        // given
        User user = User.builder()
                .id(1L)
                .email("member1@test.com")
                .nickname("member1")
                .address("Seoul")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .build();

        // when
        user = user.certificate("aaaa-aaaa-aaaa-aaaa");

        // then
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @DisplayName("User는 잘못된 인증 코드로 계정을 활성화 시 예외가 발생한다.")
    @Test
    void certificate_invalid_code() {
        // given
        User user = User.builder()
                .id(1L)
                .email("member1@test.com")
                .nickname("member1")
                .address("Seoul")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .build();

        // when
        // then
        assertThatThrownBy(() -> user.certificate("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}
