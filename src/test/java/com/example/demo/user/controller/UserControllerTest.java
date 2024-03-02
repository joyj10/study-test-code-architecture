package com.example.demo.user.controller;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.response.MyProfileResponse;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static com.example.demo.user.domain.UserStatus.ACTIVE;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


class UserControllerTest {
    @DisplayName("특정 사용자의 정보를 조회한다.(개인정보 - 주소 제외)")
    @Test
    void getUserById() {
        //given
        TestContainer testContainer = TestContainer.builder().build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("member1@test.com")
                .nickname("member1")
                .address("Seoul")
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(0L)
                .build());

        //when
        ResponseEntity<UserResponse> result = testContainer.userController.getUserById(1);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1L);
        assertThat(result.getBody().getEmail()).isEqualTo("member1@test.com");
        assertThat(result.getBody().getNickname()).isEqualTo("member1");
        assertThat(result.getBody().getStatus()).isEqualTo(ACTIVE);
    }


    @DisplayName("존재하지 않는 사용자의 정보 조회시 예외가 발생한다.")
    @Test
    void getUserById_empty() throws Exception {
        //given
        TestContainer testContainer = TestContainer.builder().build();
        // when
        // then
        assertThatThrownBy(() -> {
            testContainer.userController.getUserById(1);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("사용자는 인증코드로 계정을 활성화 할 수 있다.")
    @Test
    void verifyEmail() throws Exception {
        //given
        TestContainer testContainer = TestContainer.builder().build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("member1@test.com")
                .nickname("member1")
                .address("Seoul")
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(0L)
                .build());
        // when
        ResponseEntity<Void> result = testContainer.userController.verifyEmail(1, "aaaa-aaaa-aaaa-aaaa");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(302));
        assertThat(testContainer.userRepository.getById(1).getStatus()).isEqualTo(UserStatus.ACTIVE);
    }


    @DisplayName("사용자는 인증코드가 일치하지 않을 경우 권한 없음 예외를 던진다.")
    @Test
    void verifyEmail_exception() throws Exception {
        //given
        TestContainer testContainer = TestContainer.builder().build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("member1@test.com")
                .nickname("member1")
                .address("Seoul")
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(0L)
                .build());
        // when // then
        assertThatThrownBy(() -> {
            testContainer.userController.verifyEmail(1, "aaaa-aaaa-aaaa-aaaa1");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }

    @DisplayName("사용자 자신의 정보를 조회한다.")
    @Test
    void getMyInfo() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(() -> 1678530673958L)
                .build();

        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("member1@test.com")
                .nickname("member1")
                .address("Seoul")
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(0L)
                .build());

        // when
        ResponseEntity<MyProfileResponse> result = testContainer.userController.getMyInfo("member1@test.com");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1L);
        assertThat(result.getBody().getEmail()).isEqualTo("member1@test.com");
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(1678530673958L);
        assertThat(result.getBody().getNickname()).isEqualTo("member1");
        assertThat(result.getBody().getStatus()).isEqualTo(ACTIVE);
    }

    @DisplayName("사용자 자신의 정보를 수정할 수 있다.")
    @Test
    void updateMyInfo() throws Exception {
        /// given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(() -> 1678530673958L)
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("member1@test.com")
                .nickname("member1")
                .address("Seoul")
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(0L)
                .build());

        // when
        ResponseEntity<MyProfileResponse> result = testContainer.userController
                .updateMyInfo("member1@test.com", UserUpdate.builder()
                .address("Pangyo")
                .nickname("member1-n")
                .build());

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1L);
        assertThat(result.getBody().getEmail()).isEqualTo("member1@test.com");
        assertThat(result.getBody().getNickname()).isEqualTo("member1-n");
        assertThat(result.getBody().getAddress()).isEqualTo("Pangyo");
        assertThat(result.getBody().getStatus()).isEqualTo(ACTIVE);
    }
}
