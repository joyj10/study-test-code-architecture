package com.example.demo.user.service;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.FakeMailSender;
import com.example.demo.mock.FakeUserRepository;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

class UserReadServiceImplTest {
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void init() {
        FakeMailSender fakeMailSender = new FakeMailSender();
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        this.userServiceImpl = UserServiceImpl.builder()
                .uuidHolder(new TestUuidHolder("aaaa-aaaa-aaaa-aaaa"))
                .clockHolder(new TestClockHolder(1678530673958L))
                .userRepository(fakeUserRepository)
                .certificationService(new CertificationService(fakeMailSender))
                .build();
        fakeUserRepository.save(User.builder()
                        .id(1L)
                        .email("member1@test.com")
                        .nickname("member1")
                        .address("Seoul")
                        .certificationCode("aaaa-aaaa-aaaa-aaaa")
                        .status(UserStatus.ACTIVE)
                        .lastLoginAt(0L)
                        .build());
        fakeUserRepository.save(User.builder()
                .id(2L)
                .email("member2@test.com")
                .nickname("member2")
                .address("Seoul")
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .status(UserStatus.PENDING)
                .lastLoginAt(0L)
                .build());
    }

    @DisplayName("이메일로 유저 조회 시 ACTIVE 상태의 유저가 조회된다.")
    @Test
    void getByEmail_Active() {
        /// given
        String email = "member1@test.com";

        // when
        User result = userServiceImpl.getByEmail(email);

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
                () -> userServiceImpl.getByEmail(email))
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

        // when
        User save = userServiceImpl.create(createDto);

        // then
        assertThat(save).isNotNull();
        assertThat(save.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(save.getEmail()).isEqualTo(createDto.getEmail());
        assertThat(save.getCertificationCode()).isEqualTo("aaaa-aaaa-aaaa-aaaa");
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
        User userEntity = userServiceImpl.update(1, updateDto);

        // then
        assertThat(userEntity.getNickname()).isEqualTo(updateDto.getNickname());
        assertThat(userEntity.getAddress()).isEqualTo(updateDto.getAddress());
    }

    @DisplayName("로그인 시 마지막 로그인 시간이 변경된다.")
    @Test
    void login() {
        // given
        // when
        userServiceImpl.login(1);
        // then
        User user = userServiceImpl.getById(1);
        assertThat(user.getLastLoginAt()).isGreaterThan(0L);
        assertThat(user.getLastLoginAt()).isEqualTo(1678530673958L);
    }

    @DisplayName("PENDING 상태 사용자는 인증 코드로 ACTIVE로 상태값이 변경된다.")
    @Test
    void verifyEmail() {
        // given
        String code = "aaaa-aaaa-aaaa-aaaa";

        // when
        userServiceImpl.verifyEmail(2, code);

        // then
        User findUser = userServiceImpl.getById(2);
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
                () -> userServiceImpl.verifyEmail(2, code))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}
