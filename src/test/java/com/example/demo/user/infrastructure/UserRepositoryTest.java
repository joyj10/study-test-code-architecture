package com.example.demo.user.infrastructure;

import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest(showSql = true)
@TestPropertySource("classpath:test-application.properties")
@Sql("/sql/user-repository-test-data.sql")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @DisplayName("UserRepository 정상적으로 연결된다.")
    @Test
    void connectUserRepository() {
        // given
        // when then
        assertThat(1).isNotNull();
    }

    @DisplayName("user id와 status 일치되는 데이터가 있는 경우 유저 상태에 따라 조회가 가능하다.")
    @Test
    void findByIdAndStatus() {
        // given
        // when
        Optional<UserEntity> result = userRepository.findByIdAndStatus(1, UserStatus.ACTIVE);

        // then
        assertThat(result).isPresent();
    }

    @DisplayName("user id와 status 일치되는 데이터 없으면 조회 시 optional empty 리턴된다.")
    @Test
    void findByIdAndStatus_empty() {
        // given
        // when
        Optional<UserEntity> result = userRepository.findByIdAndStatus(1, UserStatus.PENDING);

        // then
        assertThat(result).isNotPresent();
    }


    @DisplayName("user email 과 status 일치되는 데이터가 조회된다.")
    @Test
    void findByEmailAndStatus() {
        // given
        // when
        Optional<UserEntity> result = userRepository.findByEmailAndStatus("member@test.com", UserStatus.ACTIVE);

        // then
        assertThat(result).isPresent();
    }

    @DisplayName("user email 과 status 일치되는 데이터가 없으면 empty 리턴된다.")
    @Test
    void findByEmailAndStatus_empty() {
        // given
        // when
        Optional<UserEntity> result = userRepository.findByEmailAndStatus("member@test.com", UserStatus.PENDING);

        // then
        assertThat(result).isEmpty();
    }
}
