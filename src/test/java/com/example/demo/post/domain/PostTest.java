package com.example.demo.post.domain;

import com.example.demo.mock.TestClockHolder;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @DisplayName("PostCreate로 게시물을 만들 수 있다.")
    @Test
    void fromPostCreate() {
        TestClockHolder testClockHolder = new TestClockHolder(1678530673958L);
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("helloworld")
                .build();
        User writer = User.builder()
                .id(1L)
                .email("member1@test.com")
                .nickname("member1")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .build();

        // when
        Post post = Post.from(writer, postCreate, testClockHolder);

        // then
        assertThat(post.getContent()).isEqualTo("helloworld");
        assertThat(post.getWriter().getEmail()).isEqualTo(writer.getEmail());
        assertThat(post.getWriter().getNickname()).isEqualTo(writer.getNickname());
        assertThat(post.getWriter().getAddress()).isEqualTo(writer.getAddress());
        assertThat(post.getWriter().getStatus()).isEqualTo(writer.getStatus());
        assertThat(post.getWriter().getCertificationCode()).isEqualTo(writer.getCertificationCode());
    }

    @DisplayName("PostUpdate 게시물을 수정 할 수 있다.")
    @Test
    void updatePost() {
        // given
        PostUpdate postUpdate = PostUpdate.builder()
                .content("foobar")
                .build();

        User writer = User.builder()
                .id(1L)
                .email("member1@test.com")
                .nickname("member1")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .build();

        Post post = Post.builder()
                .id(1L)
                .content("helloworld")
                .createdAt(1678530673958L)
                .modifiedAt(0L)
                .writer(writer)
                .build();

        // when
        post = post.update(postUpdate, new TestClockHolder(1679530673958L));

        // then
        assertThat(post.getContent()).isEqualTo("foobar");
        assertThat(post.getModifiedAt()).isEqualTo(1679530673958L);
    }

}
