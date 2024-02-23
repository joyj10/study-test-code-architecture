package com.example.demo.post.controller.response;

import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostResponseTest {
    @DisplayName("Post로 응답을 생성할 수 있다.")
    @Test
    void convert() {
        // given
        Post post = Post.builder()
                .content("helloworld")
                .writer(User.builder()
                        .id(1L)
                        .email("member1@test.com")
                        .nickname("member1")
                        .address("Seoul")
                        .status(UserStatus.ACTIVE)
                        .certificationCode("aaaa-aaaa-aaaa-aaaa")
                        .build())
                .build();

        // when
        PostResponse postResponse = PostResponse.from(post);

        // then
        assertThat(postResponse.getContent()).isEqualTo("helloworld");
        assertThat(postResponse.getWriter().getEmail()).isEqualTo(post.getWriter().getEmail());
        assertThat(postResponse.getWriter().getNickname()).isEqualTo(post.getWriter().getNickname());
        assertThat(postResponse.getWriter().getStatus()).isEqualTo(post.getWriter().getStatus());
    }

}
