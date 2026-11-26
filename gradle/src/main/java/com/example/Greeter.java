package com.example;

import org.apache.commons.lang3.StringUtils;

/**
 * Builds greetings.
 *
 * @author Moritz Halbritter
 */
public final class Greeter {

	private Greeter() {
	}

	/**
	 * Greets the given name, e.g. {@code "world"} -> {@code "Hello, World!"}.
	 * @param name the name
	 * @return the greeting
	 */
	public static String greet(String name) {
		return "Hello, " + StringUtils.capitalize(name) + "!";
	}

	public static void main(String[] args) {
		System.out.println(greet("world"));
	}

}
