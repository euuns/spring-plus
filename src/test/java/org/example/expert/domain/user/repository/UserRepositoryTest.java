package org.example.expert.domain.user.repository;

import com.navercorp.fixturemonkey.FixtureMonkey;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.expert.config.JpaConfig;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DataJpaTest
@Import(JpaConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Order(1)
    void 유저_데이터_생성() {

        FixtureMonkey fixtureMonkey = FixtureMonkey.create();
        AtomicLong idGenerator = new AtomicLong(1);

        List<User> users = fixtureMonkey.giveMeBuilder(User.class)
                .set("id", idGenerator.getAndIncrement())
                .setLazy("email", () -> RandomStringUtils.randomAlphanumeric(9) + "@gmail.com")
                .set("password", "password123!")
                .set("userRole", UserRole.ROLE_USER)
                .set("nickname", RandomStringUtils.randomAlphanumeric(9))
                .setNull("createdAt")
                .setNull("modifiedAt")
                .sampleList(100);

        // 100만 건의 user를 1만개 씩 나눠서 저장
        userRepository.bulkInsert(users, 10);
        userRepository.save(new User("email@email.com", "password123!", UserRole.ROLE_USER, "nickname"));

        jdbcTemplate.execute("OPTIMIZE TABLES users");
    }

    private long startTime;

    @BeforeEach
    void setUp() {
        startTime = System.currentTimeMillis();
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println(testInfo.getDisplayName() + " 실행 시간: " + duration + "ms");
    }

    @Test
    @Order(2)
    @DisplayName("일반적인 조회")
    void 유저_닉네임_검색1() {
        // 제일 마지막에 저장한 값을 이용해 조회
        String nickname = "nickname";
        User getUser = userRepository.findByNickname(nickname);

        assertEquals(nickname, getUser.getNickname());
    }

    @Test
    @Order(3)
    @DisplayName("FullText 인덱스 조회")
    void 유저_닉네임_검색2() {
        // 제일 마지막에 저장한 값을 이용해 조회
        String nickname = "nickname";
        User getUser = userRepository.findByNicknameOnFullTextIndexing(nickname);

        assertEquals(nickname, getUser.getNickname());
    }

}