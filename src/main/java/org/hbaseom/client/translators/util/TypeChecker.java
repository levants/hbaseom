package org.hbaseom.client.translators.util;

/**
 * Utility class to check appropriate java types
 * 
 * @author levan
 * 
 */
public final class TypeChecker {

	private TypeChecker() {
		throw new AssertionError("Can not initialize object");
	}

	public static boolean isByteArray(String name) {
		return name.equals("[B") || name.equals("[Ljava.lang.Byte;");
	}

	public static boolean isByte(String name) {
		return name.equals("Byte") || name.equals("java.lang.Byte")
				|| name.equals("byte");
	}

	public static boolean isBoolean(String name) {
		return name.equals("Boolean") || name.equals("java.lang.Boolean")
				|| name.equals("boolean");
	}

	public static boolean isShort(String name) {
		return name.equals("Short") || name.equals("java.lang.Short")
				|| name.equals("short");
	}

	public static boolean isInteger(String name) {
		return name.equals("Integer") || name.equals("java.lang.Integer")
				|| name.equals("int");
	}

	public static boolean isLong(String name) {
		return name.equals("Long") || name.equals("java.lang.Long")
				|| name.equals("long");
	}

	public static boolean isFloat(String name) {
		return name.equals("Float") || name.equals("java.lang.Float")
				|| name.equals("float");
	}

	public static boolean isDouble(String name) {
		return name.equals("Double") || name.equals("java.lang.Double")
				|| name.equals("double");
	}

	public static boolean isCharacter(String name) {
		return name.equals("Char") || name.equals("java.lang.Character")
				|| name.equals("char");
	}

	public static boolean isString(String name) {
		return name.equals("String") || name.equals("string")
				|| name.equals("java.lang.String");
	}
}
