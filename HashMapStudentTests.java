import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * HashMapStudentTests
 *
 * These tests are NOT exhaustive.
 * You should definitely write your own.
 *
 * @author CS 1332 TAs
 * @version 1.3
 */
public class HashMapStudentTests {

    private HashMap<MyString, String> directory;
    private static final int TIMEOUT = 200;

    @Before
    public void setUp() {
        directory = new HashMap<>();
    }

    @Test(timeout = TIMEOUT)
    public void testAdd() {
        directory.add(new MyString("Jonathan"), "TA: 1332");
        directory.add(new MyString("Monica"), "Professor: 2050");
        directory.add(new MyString("Mary"), "Professor: 1332");
        String prof = directory.add(new MyString("Monica"), "Professor: 1332");
        assertEquals("Professor: 2050", prof);
        directory.add(new MyString("Saikrishna*"), "TA: 1332");
        directory.add(new MyString("BestLang"), "PHP");
        String badLanguage = directory.add(new MyString("BestLang"), "Swift");
        assertEquals("PHP", badLanguage);
        // Here's why:
        // https://eev.ee/blog/2012/04/09/php-a-fractal-of-bad-design/

        assertEquals(5, directory.size());

        MapEntry<MyString, String>[] expected = expectedAddStuffBacking();
        assertArrayEquals(expected, directory.getTable());
    }

    @Test (timeout = TIMEOUT)
    public void testAddWithProbe() {
        directory.add(new MyString("Carey"), "TA: 1332");
        directory.add(new MyString("Julia"), "TA: 1332 (again)");
        directory.add(new MyString("Siddu"), "TA: 1332 (another one)");

        assertEquals(3, directory.size());

        MapEntry<MyString, String>[] expected =
                (MapEntry<MyString, String>[]) new MapEntry[9];

        expected[5] = new MapEntry<>(new MyString("Carey"), "TA: 1332");
        expected[6] = new MapEntry<>(new MyString("Julia"), "TA: 1332 (again)");
        expected[7] = new MapEntry<>(new MyString("Siddu"),
                "TA: 1332 (another one)");

        assertArrayEquals(expected, directory.getTable());
    }

    @Test(timeout = TIMEOUT)
    public void testResize() {
        addStuff();

        directory.add(new MyString("Stuart"), "BANANA!");
        directory.add(new MyString("Bob"), "BeeDooBeeDooBeeDoo");
        directory.add(new MyString("Kevin"), "Scarlet!");

        MapEntry<MyString, String>[] expected =
                (MapEntry<MyString, String>[]) new MapEntry[19];

        expected[3] = new MapEntry<>(new MyString("Bob"), "BeeDooBeeDooBeeDoo");
        expected[4] = new MapEntry<>(new MyString("Mary"), "Professor: 1332");
        expected[5] = new MapEntry<>(new MyString("Kevin"), "Scarlet!");
        expected[6] = new MapEntry<>(new MyString("Stuart"), "BANANA!");
        expected[7] = new MapEntry<>(new MyString("Monica*"),
                "Professor: 1332");
        expected[8] = new MapEntry<>(new MyString("Jonathan"), "TA: 1332");
        expected[10] = new MapEntry<>(new MyString("Saikrishna"), "TA: 1332");

        assertArrayEquals(expected, directory.getTable());
        assertEquals(7, directory.size());
    }

    @Test(timeout = TIMEOUT)
    public void testRemove() {
        addStuff();

        assertEquals("TA: 1332", directory.remove(new MyString("Jonathan")));

        MapEntry<MyString, String>[] actuals = directory.getTable();
        assertNotNull(actuals[8]);
        assertTrue(actuals[8].isRemoved());
        assertEquals(3, directory.size());
    }

    @Test(timeout = TIMEOUT)
    public void testRemoveEdgeCase() {
        /*
         ***********************************
         * THERE ARE MANY OTHER EDGE CASES *
         *     THIS IS ONLY ONE OF THEM    *
         ***********************************
         */

        // This part is the same as testRemove()
        addStuff();

        assertEquals("TA: 1332", directory.remove(new MyString("Jonathan")));

        MapEntry<MyString, String>[] actuals = directory.getTable();
        assertNotNull(actuals[8]);
        assertTrue(actuals[8].isRemoved());
        assertEquals(3, directory.size());

        // Here's the edge case part
        directory.add(new MyString("Raymond*"), "TA: 1332*");
        actuals = directory.getTable();
        assertNotNull(actuals[8]);
        assertFalse(actuals[8].isRemoved());
        assertEquals(actuals[8], new MapEntry<>(new MyString("Raymond*"),
                "TA: 1332*"));
    }

    @Test(timeout = TIMEOUT)
    public void testGet() {
        addStuff();
        assertEquals("TA: 1332", directory.get(new MyString("Saikrishna")));
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void testGetNull() {
        directory.get(null);
    }

    @Test(timeout = TIMEOUT, expected = NoSuchElementException.class)
    public void testGetNotFound() {
        directory.get(new MyString(""));
    }

    /**
     * Add a baseline of items to the hash map.
     */
    private void addStuff() {
        directory.add(new MyString("Jonathan"), "TA: 1332");
        directory.add(new MyString("Monica*"), "Professor: 1332");
        directory.add(new MyString("Mary"), "Professor: 1332");
        directory.add(new MyString("Saikrishna"), "TA: 1332");
    }

    /**
     * Get the expected positions and ordering of the entries in the hash map.
     *
     * @return array with expected entries with the baseline
     */
    private static MapEntry<MyString, String>[] expectedAddStuffBacking() {
        MapEntry<MyString, String>[] expected =
                (MapEntry<MyString, String>[]) new MapEntry[9];

        expected[0] = new MapEntry<>(new MyString("BestLang"), "Swift");
        expected[2] = new MapEntry<>(new MyString("Saikrishna*"), "TA: 1332");
        expected[4] = new MapEntry<>(new MyString("Mary"), "Professor: 1332");
        expected[6] = new MapEntry<>(new MyString("Monica"), "Professor: 1332");
        expected[8] = new MapEntry<>(new MyString("Jonathan"), "TA: 1332");
        return expected;
    }

    private static class MyString {
        private String s;

        /**
         * Create a wrapper object around a String object, for the purposes
         * of controlling the hash code.
         *
         * @param s string to store in this object
         */
        public MyString(String s) {
            this.s = s;
        }

        @Override
        public int hashCode() {
            return s.length();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof MyString) {
                return s.equals(((MyString) o).s);
            }
            if (o instanceof String) {
                return s.equals(o);
            }
            return false;
        }

        @Override
        public String toString() {
            return s;
        }
    }
}
