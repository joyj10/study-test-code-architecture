package com.example.demo.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.demo.model.dto.PostCreateDto;
import com.example.demo.model.dto.PostUpdateDto;
import com.example.demo.repository.PostEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
        @Sql(value = "/sql/post-service-test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
})
class PostServiceTest {

    @Autowired
    private PostService postService;

    @DisplayName("특정 id로 조회 시 게시물이 조회 된다.")
    @Test
    void getById() {
        // given
        // when
        PostEntity result = postService.getById(1);

        // then
        assertThat(result.getContent()).isEqualTo("helloworld");
    }

    @DisplayName("postCreateDto 이용해여 게시물을 생성할 수 있다.")
    @Test
    void create() {
        // given
        PostCreateDto postCreateDto = PostCreateDto.builder()
                .writerId(1)
                .content("foobar")
                .build();

        // when
        PostEntity result = postService.create(postCreateDto);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getContent()).isEqualTo("foobar");
        assertThat(result.getCreatedAt()).isPositive();
    }

    @DisplayName("postUpdateDto 이용하여 게시물을 수정할 수 있다.")
    @Test
    void update() {
        // given
        PostUpdateDto postUpdateDto = PostUpdateDto.builder()
                .content("hello world :)")
                .build();

        // when
        postService.update(1, postUpdateDto);

        // then
        PostEntity postEntity= postService.getById(1);
        assertThat(postEntity.getContent()).isEqualTo("hello world :)");
        assertThat(postEntity.getModifiedAt()).isPositive();
    }

}
