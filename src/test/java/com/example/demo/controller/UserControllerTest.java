package com.example.demo.controller;

import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserResponse;
import com.example.demo.model.dto.UserUpdateDto;
import com.example.demo.repository.UserEntity;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SqlGroup({
        @Sql(value = "/sql/user-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("특정 사용자의 정보를 조회한다.(개인정보 - 주소 제외)")
    @Test
    void getUserById() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("member1@test.com"))
                .andExpect(jsonPath("$.nickname").value("member1"))
                .andExpect(jsonPath("$.address").doesNotExist())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }


    @DisplayName("존재하지 않는 사용자의 정보 조회시 예외가 발생한다.")
    @Test
    void getUserById_empty() throws Exception {
        mockMvc.perform(get("/api/users/1111"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Users에서 ID 1111를 찾을 수 없습니다."));
    }

    @DisplayName("사용자는 인증코드로 계정을 활성화 할 수 있다.")
    @Test
    void verifyEmail() throws Exception {
        mockMvc.perform(
                get("/api/users/2/verify")
                        .queryParam("certificationCode", "aaaa-aaaa-aaaa-aaaa"))
                .andExpect(status().isFound());

        UserEntity userEntity = userRepository.findById(2L).orElseThrow();
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }


    @DisplayName("사용자는 인증코드가 일치하지 않을 경우 권한 없음 예외를 던진다.")
    @Test
    void verifyEmail_exception() throws Exception {
        mockMvc.perform(
                        get("/api/users/2/verify")
                                .queryParam("certificationCode", "aaaa-aaaa-aaaa-aaaa11111"))
                .andExpect(status().isForbidden());
    }

    @DisplayName("사용자 자신의 정보를 조회한다.")
    @Test
    void getMyInfo() throws Exception {
        mockMvc.perform(
                get("/api/users/me")
                        .header("EMAIL", "member1@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("member1@test.com"))
                .andExpect(jsonPath("$.nickname").value("member1"))
                .andExpect(jsonPath("$.address").value("Seoul"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @DisplayName("사용자 자신의 정보를 수정할 수 있다.")
    @Test
    void updateMyInfo() throws Exception {
        // given
        String changedNickname = "member1-new";
        String changedAddress = "Pangyo";
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .nickname(changedNickname)
                .address(changedAddress)
                .build();

        // when then
        mockMvc.perform(
                        put("/api/users/me")
                                .header("EMAIL", "member1@test.com")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("member1@test.com"))
                .andExpect(jsonPath("$.nickname").value(changedNickname))
                .andExpect(jsonPath("$.address").value(changedAddress))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}
