package com.example.demo.repository;

import com.example.demo.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest(showSql = true)
@TestPropertySource("classpath:test-application.properties")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private UserEntity saveUserEntity;

    @BeforeEach
    void init() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("micool1030@gmail.com");
        userEntity.setAddress("Seoul");
        userEntity.setNickname("cool");
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity.setCertificationCode("aaaa-aaaa-aaaa-aaaa");
        saveUserEntity = userRepository.save(userEntity);
    }

    @DisplayName("UserRepository 정상적으로 연결된다.")
    @Test
    void connectUserRepository() {
        // given
        // when then
        assertThat(saveUserEntity.getId()).isNotNull();
    }

    @DisplayName("user id와 status 일치되는 데이터가 있는 경우 유저 상태에 따라 조회가 가능하다.")
    @Test
    void findByIdAndStatus() {
        // given
        // when
        Optional<UserEntity> result = userRepository.findByIdAndStatus(saveUserEntity.getId(), UserStatus.ACTIVE);

        // then
        assertThat(result.isPresent()).isTrue();
    }

    @DisplayName("user id와 status 일치되는 데이터 없으면 조회 시 optional empty 리턴된다.")
    @Test
    void findByIdAndStatus_empty() {
        // given
        // when
        Optional<UserEntity> result = userRepository.findByIdAndStatus(saveUserEntity.getId(), UserStatus.PENDING);

        // then
        assertThat(result.isEmpty()).isTrue();
    }


    @DisplayName("user email 과 status 일치되는 데이터가 조회된다.")
    @Test
    void findByEmailAndStatus() {
        // given
        // when
        Optional<UserEntity> result = userRepository.findByEmailAndStatus(saveUserEntity.getEmail(), UserStatus.ACTIVE);

        // then
        assertThat(result.isPresent()).isTrue();
    }

    @DisplayName("user email 과 status 일치되는 데이터가 없으면 empty 리턴된다.")
    @Test
    void findByEmailAndStatus_empty() {
        // given
        // when
        Optional<UserEntity> result = userRepository.findByEmailAndStatus(saveUserEntity.getEmail(), UserStatus.PENDING);

        // then
        assertThat(result.isEmpty()).isTrue();
    }
}