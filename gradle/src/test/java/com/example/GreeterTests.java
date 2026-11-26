package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GreeterTests {

	@Test
	void greetCapitalizesName() {
		assertEquals("Hello, World!", Greeter.greet("world"));
	}

}
