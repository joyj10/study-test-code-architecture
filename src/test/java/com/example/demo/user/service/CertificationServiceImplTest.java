package com.example.demo.user.service;

import com.example.demo.mock.FakeMailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CertificationServiceImplTest {

    @DisplayName("이메일과 컨텐츠가 제대로 만들어져서 보내진다.")
    @Test
    void send() {
        // given
        FakeMailSender fakeMailSender = new FakeMailSender();
        CertificationServiceImpl certificationServiceImpl = new CertificationServiceImpl(fakeMailSender);

        // when
        String mail = "member1@test.com";
        String certificationCode = "aaaa-aaaa-aaaa-aaaa";
        int userId = 1;
        certificationServiceImpl.send(mail, userId, certificationCode);

        // then
        assertThat(fakeMailSender.email).isEqualTo(mail);
        assertThat(fakeMailSender.title).isEqualTo("Please certify your email address");
        assertThat(fakeMailSender.content).isEqualTo("Please click the following link to certify your email address: http://localhost:8080/api/users/" + userId + "/verify?certificationCode=" + certificationCode);
    }

}
