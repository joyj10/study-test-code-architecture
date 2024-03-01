package com.example.demo.post.service;

import com.example.demo.mock.FakePostRepository;
import com.example.demo.mock.FakeUserRepository;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PostServiceImplTest {
    private PostServiceImpl postServiceImpl;

    @BeforeEach
    void init() {
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        FakePostRepository fakePostRepository = new FakePostRepository();
        this.postServiceImpl = PostServiceImpl.builder()
                .userRepository(fakeUserRepository)
                .postRepository(fakePostRepository)
                .clockHolder(new TestClockHolder(1678530673958L))
                .build();

        User user1 = User.builder()
                .id(1L)
                .email("member1@test.com")
                .nickname("member1")
                .address("Seoul")
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(0L)
                .build();
        fakeUserRepository.save(user1);
        fakeUserRepository.save(User.builder()
                .id(2L)
                .email("member2@test.com")
                .nickname("member2")
                .address("Seoul")
                .certificationCode("aaaa-aaaa-aaaa-aaaa")
                .status(UserStatus.PENDING)
                .lastLoginAt(0L)
                .build());
        fakePostRepository.save(Post.builder()
                        .id(1L)
                        .content("helloworld")
                        .createdAt(1678530673958L)
                        .modifiedAt(0L)
                        .writer(user1)
                        .build());
    }

    @DisplayName("특정 id로 조회 시 게시물이 조회 된다.")
    @Test
    void getById() {
        // given
        // when
        Post result = postServiceImpl.getById(1);

        // then
        assertThat(result.getContent()).isEqualTo("helloworld");
    }

    @DisplayName("postCreateDto 이용해여 게시물을 생성할 수 있다.")
    @Test
    void create() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("foobar")
                .build();

        // when
        Post result = postServiceImpl.create(postCreate);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getContent()).isEqualTo("foobar");
        assertThat(result.getCreatedAt()).isEqualTo(1678530673958L);
    }

    @DisplayName("postUpdateDto 이용하여 게시물을 수정할 수 있다.")
    @Test
    void update() {
        // given
        PostUpdate postUpdate = PostUpdate.builder()
                .content("hello world :)")
                .build();

        // when
        postServiceImpl.update(1, postUpdate);

        // then
        Post post = postServiceImpl.getById(1);
        assertThat(post.getContent()).isEqualTo("hello world :)");
        assertThat(post.getModifiedAt()).isEqualTo(1678530673958L);
    }

}
