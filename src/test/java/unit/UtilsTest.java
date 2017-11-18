/**
 * 
 */
package unit;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import comp3111.Utils;

/**
 * @author Forsythe
 *
 */
public class UtilsTest {

	@Test
	public void testCollectionContainsIgnoreCase() {

		ArrayList<String> arr = new ArrayList<String>();
		arr.add("hithere");
		arr.add("longstring");
		arr.add("ilovecomp3111");

		assertTrue(Utils.collectionContainsIgnoreCase(arr, "it"));
		assertFalse(Utils.collectionContainsIgnoreCase(arr, "@@"));

	}

	@Test
	public void testIterableToCollection() {
		Iterable<Integer> iterable = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
		assertSame(3, Utils.iterableToCollection(iterable).size());
	}

	@Test
	public void testSafeParseDoubleEquals() {
		assertTrue(Utils.safeParseDoubleEquals(2.0, "2.0"));
		assertFalse(Utils.safeParseDoubleEquals(2.0, "3"));
		assertFalse(Utils.safeParseDoubleEquals(2.0, "p"));
	}

	@Test
	public void testSafeParseIntEquals() {
		assertTrue(Utils.safeParseIntEquals(2, "2"));
		assertFalse(Utils.safeParseIntEquals(2, "3"));
		assertFalse(Utils.safeParseIntEquals(2, "p"));
	}

	@Test
	public void testSafeParseBoolEquals() {
		assertTrue(Utils.safeParseBoolEquals(true, "true"));
		assertFalse(Utils.safeParseBoolEquals(true, "false"));
		assertFalse(Utils.safeParseBoolEquals(true, "p"));
	}

	@Test
	public void testSafeParseLongEquals() {
		assertTrue(Utils.safeParseLongEquals(1L, "1"));
		assertFalse(Utils.safeParseLongEquals(2L, "1"));
		assertFalse(Utils.safeParseLongEquals(2L, "p"));
	}

	@Test
	public void testConvertdayOfWeekBetweenIntAndString() {
		for (int k = 1; k <= 7; k++) {
			assertSame(k, Utils.stringToDay(Utils.dayToString(k)));
		}
		assertSame(-1, Utils.stringToDay("wtf"));
		assertEquals("invalid day", Utils.dayToString(-1));
	}
}
