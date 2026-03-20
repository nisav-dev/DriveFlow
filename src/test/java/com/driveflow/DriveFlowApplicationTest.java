package com.driveflow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("בדיקת טעינת ApplicationContext")
class DriveFlowApplicationTest {

    @Test
    @DisplayName("Spring ApplicationContext נטען בהצלחה")
    void contextLoads() {
        // Spring context loads without exceptions
    }
}
