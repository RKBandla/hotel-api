package com.example.hotel_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// app.seed-admin=false: don't try to create the admin user (no database needed for this test)
@SpringBootTest(properties = "app.seed-admin=false")
class HotelApiApplicationTests {

	@Test
	void contextLoads() {
	}
}