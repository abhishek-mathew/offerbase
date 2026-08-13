package com.offerbase.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        properties = {
                "JWT_SECRET=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
                "GOOGLE_CLIENT_ID=test-google-client-id",
                "GOOGLE_CLIENT_SECRET=test-google-client-secret"
        }
)

class BackendApplicationTests {

    @Test
    void contextLoads() {
    }
}