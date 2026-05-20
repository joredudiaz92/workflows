package com.workflows.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WorkflowApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainMethodTest() {
		WorkflowApplication.main(new String[] {"--spring.profiles.active=test"});
	}
}
