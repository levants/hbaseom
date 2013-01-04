package org.hbaseom.client.translators.util;

import static org.hbaseom.client.meta.reflect.Reflector.getGetter;
import static org.hbaseom.client.meta.reflect.Reflector.getSetter;
import static org.hbaseom.client.meta.reflect.Reflector.invokeMethod;
import static org.hbaseom.client.meta.reflect.Reflector.isAnnotated;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 
 * @author levan
 * 
 */
public class UpdateHelper {

	private static boolean checkFieldOnOmit(Field fieldToCheck,
			String... nameToOmit) {
		for (String omitName : nameToOmit) {
			if (omitName.equals(fieldToCheck.getName())) {
				return true;
			}
		}

		return false;
	}

	private static Field getMatchField(Field field1, Field... fields2) {
		Field matchField = null;
		for (Field field2 : fields2) {
			if (field2.getName().equals(field1.getName())) {
				matchField = field2;
				break;
			}
		}
		return matchField;
	}

	public static void fillObject(Object object1, Object object2,
			String... nameToOmit) {
		Class<?> clazz1 = object1.getClass();
		Class<?> clazz2 = object2.getClass();
		Field[] fields1 = clazz1.getDeclaredFields();
		Field[] fields2 = clazz1.getDeclaredFields();
		boolean omit;
		for (Field field1 : fields1) {
			if (!isAnnotated(field1)) {
				continue;
			}
			omit = checkFieldOnOmit(field1, nameToOmit);
			if (omit) {
				continue;
			}
			Field matchField = getMatchField(field1, fields2);

			if (matchField == null) {
				continue;
			}
			Method getter = getGetter(clazz1, field1);
			Method setter = getSetter(clazz2, matchField);
			Object toSet = invokeMethod(getter, object1);
			invokeMethod(setter, object2, toSet);
		}
	}
}
