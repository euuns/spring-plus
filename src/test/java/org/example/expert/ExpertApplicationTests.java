package org.example.expert;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNull;

@ActiveProfiles("test")
@SpringBootTest(classes = ExpertApplication.class)
class ExpertApplicationTests {

    @Test
    void contextLoads() {
    }

}
