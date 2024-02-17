insert into `users` (`id`, `email`, `nickname`, `address`, `certification_code`, `status`, `last_login_at`)
values (1, 'member1@test.com', 'member1', 'Seoul', 'aaaa-aaaa-aaaa-aaaa', 'ACTIVE', 0);
insert into `users` (`id`, `email`, `nickname`, `address`, `certification_code`, `status`, `last_login_at`)
values (2, 'member2@test.com', 'member2', 'Seoul', 'aaaa-aaaa-aaaa-aaaa', 'PENDING', 0);
insert into `posts`  (`id`, `content`, `created_at`, `modified_at`, `user_id`)
values (1, 'helloworld', 1678530673958, 0, 1);
