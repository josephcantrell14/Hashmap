import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * HashMapJDwireTests
 *
 * @author Joshua Dwire
 * @version 1.7.1
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HashMapJDwireTests {

    private HashMap<HackedString, String> directory;
    private static final int TIMEOUT = 200;

    @Before
    public void setUp() {
        directory = new HashMap<>();
    }

    @Test(timeout = TIMEOUT)
    public void test01SizeParam() {
        HashMap<HackedString, String> map = new HashMap<>(15);
        assertEquals(15, map.getTable().length);
    }

    @Test(timeout = TIMEOUT)
    public void test02Add() {
        directory.add(new HackedString("Item 1", 8), "Value 1a");
        directory.add(new HackedString("Item 2", 6), "Value 2a");
        directory.add(new HackedString("Item 3", 4), "Value 3a");

        String prof = directory.add(new HackedString("Item 2", 6), "Value 2b");
        assertEquals("Value 2a", prof);

        directory.add(new HackedString("Item 4", 11), "Value 4a");
        assertNull(directory.add(new HackedString("Item 5", 8), "Value 5a"));

        String oldLang = directory.add(new HackedString("Item 5", 8), "Value 5b");
        assertEquals("Value 5a", oldLang);

        assertEquals(5, directory.size());

        MapEntry<String, String>[] expected =
                (MapEntry<String, String>[]) new MapEntry[]{
                        new MapEntry<>("Item 5", "Value 5b"),
                        null,
                        new MapEntry<>("Item 4", "Value 4a"),
                        null,
                        new MapEntry<>("Item 3", "Value 3a"),
                        null,
                        new MapEntry<>("Item 2", "Value 2b"),
                        null,
                        new MapEntry<>("Item 1", "Value 1a")
                };

        assertArrayEquals(expected, directory.getTable());
    }

    @Test(timeout = TIMEOUT)
    public void testPHPUsefulness() {
        assertTrue("Java is broken", true);
        // https://blog.codinghorror.com/php-sucks-but-it-doesnt-matter/
        // See testAdd in TA tests...
        // Disclaimer: I'm a professional PHP developer
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void test03AddNullKey() {
        directory.add(null, "test");
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void test04AddNullValue() {
        directory.add(new HackedString("test", 9), null);
    }

    @Test(timeout = TIMEOUT)
    public void test05AddWithProbe() {
        directory.add(new HackedString("Item 1", 4), "Value 1");
        directory.add(new HackedString("Item 2", 4), "Value 2");
        directory.add(new HackedString("Item 3", 4), "Value 3");

        assertEquals(3, directory.size());

        MapEntry<String, String>[] expected =
                (MapEntry<String, String>[]) new MapEntry[]{
                        null,
                        null,
                        null,
                        null,
                        new MapEntry<>("Item 1", "Value 1"),
                        new MapEntry<>("Item 2", "Value 2"),
                        new MapEntry<>("Item 3", "Value 3"),
                        null,
                        null,
                };

        assertArrayEquals(expected, directory.getTable());
    }

    @Test(timeout = TIMEOUT)
    public void test06AddWithProbeWraparound() {
        directory.add(new HackedString("Item 1", 7), "Value 1");
        directory.add(new HackedString("Item 2", 7), "Value 2");
        directory.add(new HackedString("Item 3", 7), "Value 3");
        directory.add(new HackedString("Item 4", 7), "Value 4");

        assertEquals(4, directory.size());

        MapEntry<String, String>[] expected =
                (MapEntry<String, String>[]) new MapEntry[]{
                        new MapEntry<>("Item 3", "Value 3"),
                        new MapEntry<>("Item 4", "Value 4"),
                        null,
                        null,
                        null,
                        null,
                        null,
                        new MapEntry<>("Item 1", "Value 1"),
                        new MapEntry<>("Item 2", "Value 2"),
                };

        assertArrayEquals(expected, directory.getTable());
    }

    @Test(timeout = TIMEOUT)
    public void test07AddWithProbeHop() {
        directory.add(new HackedString("Item 1", 3), "Value 1");
        directory.add(new HackedString("Item 2", 3), "Value 2");
        directory.add(new HackedString("Hashcode5", 5), "asdf");
        directory.add(new HackedString("Item 3", 3), "Value 3");
        directory.add(new HackedString("Item 4", 3), "Value 4");

        assertEquals(5, directory.size());

        MapEntry<String, String>[] expected =
                (MapEntry<String, String>[]) new MapEntry[]{
                        null,
                        null,
                        null,
                        new MapEntry<>("Item 1", "Value 1"),
                        new MapEntry<>("Item 2", "Value 2"),
                        new MapEntry<>("Hashcode5", "asdf"),
                        new MapEntry<>("Item 3", "Value 3"),
                        new MapEntry<>("Item 4", "Value 4"),
                        null
                };

        assertArrayEquals(expected, directory.getTable());
    }

    @Test(timeout = TIMEOUT)
    public void test08AddWithProbeHopRemove() {
        directory.add(new HackedString("Item 1", 3), "Value 1");
        directory.add(new HackedString("Item 2", 3), "Value 2");
        directory.add(new HackedString("Hashcode5", 5), "asdf");
        directory.add(new HackedString("Item 3", 3), "Value 3");

        directory.remove(new HackedString("Hashcode5", 5));
        directory.add(new HackedString("Item 4", 3), "Value 4");

        assertEquals(4, directory.size());

        MapEntry<String, String>[] expected =
                (MapEntry<String, String>[]) new MapEntry[]{
                        null,
                        null,
                        null,
                        new MapEntry<>("Item 1", "Value 1"),
                        new MapEntry<>("Item 2", "Value 2"),
                        new MapEntry<>("Item 4", "Value 4"),
                        new MapEntry<>("Item 3", "Value 3"),
                        null,
                        null
                };

        assertArrayEquals(expected, directory.getTable());
    }

    @Test(timeout = TIMEOUT)
    public void test09AddRegrow() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");
        directory.add(new HackedString("Hash6", 6), "e");
        directory.add(new HackedString("Hash3", 3), "f");
        directory.add(new HackedString("Hash5", 5), "g");

        MapEntry<HackedString, String>[] expected =
                (MapEntry<HackedString, String>[]) new MapEntry[]{
                        null,
                        null,
                        null,
                        new MapEntry<>("Hash3", "f"),
                        new MapEntry<>("Hash4", "c"),
                        new MapEntry<>("Hash5", "g"),
                        new MapEntry<>("Hash6", "e"),
                        new MapEntry<>("Hash7", "b"),
                        new MapEntry<>("Hash8", "a"),
                        null,
                        new MapEntry<>("Hash10", "d"),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                };

        assertArrayEquals(expected, directory.getTable());
        assertEquals(7, directory.size());
    }

    @Test(timeout = TIMEOUT)
    public void test10LoadFactorComparison() {
        directory.resizeBackingTable(100);

        for (int i = 1; i < 68; i++) {
            directory.add(new HackedString("Item" + i, i), "Value" + i);
        }

        assertEquals(
                "You regrew too early. " +
                "Load factor greater than, not greater than or equal to.",
                100, directory.getTable().length);

        directory.add(new HackedString("Item68", 68), "Value68");
        assertEquals("Regrow didn't happen.", 201, directory.getTable().length);
    }

    @Test(timeout = TIMEOUT)
    public void test11ForceResize() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");

        directory.resizeBackingTable(11);

        MapEntry<HackedString, String>[] expected =
                (MapEntry<HackedString, String>[]) new MapEntry[]{
                        null,
                        null,
                        null,
                        null,
                        new MapEntry<>("Hash4", "c"),
                        null,
                        null,
                        new MapEntry<>("Hash7", "b"),
                        new MapEntry<>("Hash8", "a"),
                        null,
                        null
                };

        assertArrayEquals(expected, directory.getTable());
        assertEquals(3, directory.size());

        directory.resizeBackingTable(3);

        expected =
                (MapEntry<HackedString, String>[]) new MapEntry[]{
                        new MapEntry<>("Hash8", "a"),
                        new MapEntry<>("Hash4", "c"),
                        new MapEntry<>("Hash7", "b"),
                };

        assertEquals("Looks like you regrew the table", 3, directory.getTable().length);
        assertArrayEquals(expected, directory.getTable());
        assertEquals(3, directory.size());
    }

    @Test(timeout = TIMEOUT)
    public void test12Remove() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");

        assertEquals("a", directory.remove(new HackedString("Hash8", 8)));

        MapEntry<HackedString, String>[] actuals = directory.getTable();
        assertNotNull(actuals[8]);
        assertTrue(actuals[8].isRemoved());
        assertEquals(3, directory.size());
    }

    @Test(timeout = TIMEOUT)
    public void test13RemoveWraparound() {
        directory.add(new HackedString("Item 1", 7), "Value 1");
        directory.add(new HackedString("Item 2", 7), "Value 2");
        directory.add(new HackedString("Item 3", 7), "Value 3");
        directory.add(new HackedString("Item 4", 7), "Value 4");

        assertEquals("Value 3", directory.remove(new HackedString("Item 3", 7)));

        MapEntry<HackedString, String>[] actuals = directory.getTable();
        assertNotNull(actuals[0]);
        assertTrue(actuals[0].isRemoved());
        assertEquals(3, directory.size());
    }

    @Test(timeout = TIMEOUT, expected = NoSuchElementException.class)
    public void test14RemoveEfficient() {
        directory.add(new HackedString("Item 1", 2), "Value 1");
        directory.add(new HackedString("Item 2", 2), "Value 2");
        directory.add(new HackedString("Item 3", 2), "Value 3");

        MapEntry<HackedString, String>[] actuals = directory.getTable();
        actuals[6] = new MapEntry<>(new HackedString("Item 4", 2), "Value 4");

        // This shouldn't get found, since it's after a null
        directory.remove(new HackedString("Item 4", 2));
    }

    @Test(timeout = TIMEOUT)
    public void test15RemoveResize() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");

        assertEquals("a", directory.remove(new HackedString("Hash8", 8)));

        directory.resizeBackingTable(3);

        MapEntry<HackedString, String>[] expected =
                (MapEntry<HackedString, String>[]) new MapEntry[]{
                        new MapEntry<>("Hash7", "b"),
                        new MapEntry<>("Hash10", "d"),
                        new MapEntry<>("Hash4", "c"),
                };

        assertArrayEquals(expected, directory.getTable());
        assertEquals(3, directory.size());
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void test16RemoveNull() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");

        directory.remove(null);
    }

    @Test(timeout = TIMEOUT, expected = NoSuchElementException.class)
    public void test17RemoveNonexistent() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");

        directory.remove(new HackedString("Hash8 (again)", 8));
    }

    @Test(timeout = TIMEOUT, expected = NoSuchElementException.class)
    public void test18RemoveNonexistentFull() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");
        directory.resizeBackingTable(4);

        directory.remove(new HackedString("Hash8 (again)", 8));
    }

    @Test(timeout = TIMEOUT, expected = NoSuchElementException.class)
    public void test19RemoveTwice() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");

        assertEquals("a", directory.remove(new HackedString("Hash8", 8)));

        MapEntry<HackedString, String>[] actuals = directory.getTable();
        assertNotNull(actuals[8]);
        assertTrue(actuals[8].isRemoved());
        assertEquals(3, directory.size());

        directory.remove(new HackedString("Hash8", 8));
    }

    @Test(timeout = TIMEOUT, expected = NoSuchElementException.class)
    public void test20RemoveEmpty() {
        // Just to make sure there's not a NullReferenceException
        directory.remove(new HackedString("Hash8", 8));
    }

    @Test(timeout = TIMEOUT)
    public void test21RemoveAddDuplicate() {
        directory.resizeBackingTable(7);


        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash1", 1), "b");
        directory.add(new HackedString("Hash15", 15), "c");

        assertEquals("a", directory.remove(new HackedString("Hash8", 8)));
        assertEquals(2, directory.size());

        MapEntry<String, String>[] expected =
            (MapEntry<String, String>[]) new MapEntry[]{
                    null,
                    removed(new MapEntry<>("Hash8", "a")),
                    new MapEntry<>("Hash1", "b"),
                    new MapEntry<>("Hash15", "c"),
                    null,
                    null,
                    null,
            };
        assertArrayEquals(expected, directory.getTable());

        assertEquals("c", directory.add(new HackedString("Hash15", 15), "d"));
        assertEquals(2, directory.size());

        expected = (MapEntry<String, String>[]) new MapEntry[]{
                null,
                removed(new MapEntry<>("Hash8", "a")),
                new MapEntry<>("Hash1", "b"),
                new MapEntry<>("Hash15", "d"),
                null,
                null,
                null,
        };
        assertArrayEquals(expected, directory.getTable());
    }

    @Test(timeout = TIMEOUT)
    public void test22RemoveReadd() {
        directory.resizeBackingTable(7);


        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash1", 1), "b");
        directory.add(new HackedString("Hash15", 15), "c");

        assertEquals("b", directory.remove(new HackedString("Hash1", 1)));
        assertEquals(2, directory.size());

        MapEntry<String, String>[] expected =
        (MapEntry<String, String>[]) new MapEntry[]{
                null,
                new MapEntry<>("Hash8", "a"),
                removed(new MapEntry<>("Hash1", "b")),
                new MapEntry<>("Hash15", "c"),
                null,
                null,
                null,
        };
        assertArrayEquals(expected, directory.getTable());

        assertNull("Replacing a duplicate but removed item should return null.", directory.add(new HackedString("Hash1", 1), "d"));
        assertEquals("Incorrect size after re-adding duplicate.", 3, directory.size());

        expected =
        (MapEntry<String, String>[]) new MapEntry[]{
                null,
                new MapEntry<>("Hash8", "a"),
                new MapEntry<>("Hash1", "d"),
                new MapEntry<>("Hash15", "c"),
                null,
                null,
                null,
        };
        assertArrayEquals(expected, directory.getTable());
    }

    @Test(timeout = TIMEOUT)
    public void test23RemoveRemoveReadd() {
        directory.resizeBackingTable(7);


        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash1", 1), "b");
        directory.add(new HackedString("Hash15", 15), "c");
        directory.add(new HackedString("Hash22", 22), "d");

        assertEquals("b", directory.remove(new HackedString("Hash1", 1)));
        assertEquals(3, directory.size());

        assertEquals("c", directory.remove(new HackedString("Hash15", 15)));
        assertEquals(2, directory.size());

        MapEntry<String, String>[] expected =
        (MapEntry<String, String>[]) new MapEntry[]{
                null,
                new MapEntry<>("Hash8", "a"),
                removed(new MapEntry<>("Hash1", "b")),
                removed(new MapEntry<>("Hash15", "c")),
                new MapEntry<>("Hash22", "d"),
                null,
                null,
        };
        assertArrayEquals(expected, directory.getTable());

        assertNull("Replacing a duplicate but removed item should return null.", directory.add(new HackedString("Hash15", 15), "e"));
        assertEquals("Incorrect size after re-adding duplicate.", 3, directory.size());

        expected =
        (MapEntry<String, String>[]) new MapEntry[]{
                null,
                new MapEntry<>("Hash8", "a"),
                new MapEntry<>("Hash15", "e"),
                removed(new MapEntry<>("Hash15", "c")),
                new MapEntry<>("Hash22", "d"),
                null,
                null,
        };
        assertArrayEquals(expected, directory.getTable());
    }

    @Test(timeout = TIMEOUT)
    public void test24Get() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");

        assertEquals("a", directory.get(new HackedString("Hash8", 8)));

        MapEntry<HackedString, String>[] actuals = directory.getTable();
        assertFalse(actuals[8].isRemoved());
        assertEquals(4, directory.size());
    }

    @Test(timeout = TIMEOUT)
    public void test25GetWraparound() {
        directory.add(new HackedString("Item 1", 7), "Value 1");
        directory.add(new HackedString("Item 2", 7), "Value 2");
        directory.add(new HackedString("Item 3", 7), "Value 3");
        directory.add(new HackedString("Item 4", 7), "Value 4");

        assertEquals("Value 3", directory.get(new HackedString("Item 3", 7)));

        MapEntry<HackedString, String>[] actuals = directory.getTable();
        assertNotNull(actuals[0]);
        assertFalse(actuals[0].isRemoved());
        assertEquals(4, directory.size());
    }

    @Test(timeout = TIMEOUT, expected = NoSuchElementException.class)
    public void test26GetEfficient() {
        directory.add(new HackedString("Item 1", 2), "Value 1");
        directory.add(new HackedString("Item 2", 2), "Value 2");
        directory.add(new HackedString("Item 3", 2), "Value 3");

        MapEntry<HackedString, String>[] actuals = directory.getTable();
        actuals[6] = new MapEntry<>(new HackedString("Item 4", 2), "Value 4");

        // This shouldn't get found, since it's after a null
        directory.get(new HackedString("Item 4", 2));
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void test27GetNull() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");

        directory.get(null);
    }

    @Test(timeout = TIMEOUT, expected = NoSuchElementException.class)
    public void test28GetNonexistent() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");

        directory.get(new HackedString("Hash8 (again)", 8));
    }

    @Test(timeout = TIMEOUT, expected = NoSuchElementException.class)
    public void test29GetNonexistentFull() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");
        directory.resizeBackingTable(4);

        directory.get(new HackedString("Hash8 (again)", 8));
    }

    @Test(timeout = TIMEOUT)
    public void test30GetTwice() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");

        assertEquals("a", directory.get(new HackedString("Hash8", 8)));

        MapEntry<HackedString, String>[] actuals = directory.getTable();
        assertNotNull(actuals[8]);
        assertFalse(actuals[8].isRemoved());
        assertEquals(4, directory.size());

        assertEquals("a", directory.get(new HackedString("Hash8", 8)));
    }

    @Test(timeout = TIMEOUT, expected = NoSuchElementException.class)
    public void test31GetEmpty() {
        // Just to make sure there's not a NullReferenceException
        directory.get(new HackedString("Hash8", 8));
    }

    @Test(timeout = TIMEOUT)
    public void test32Contains() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");

        assertTrue(directory.contains(new HackedString("Hash8", 8)));

        MapEntry<HackedString, String>[] actuals = directory.getTable();
        assertFalse(actuals[8].isRemoved());
        assertEquals(4, directory.size());
    }

    @Test(timeout = TIMEOUT)
    public void test33ContainsWraparound() {
        directory.add(new HackedString("Item 1", 7), "Value 1");
        directory.add(new HackedString("Item 2", 7), "Value 2");
        directory.add(new HackedString("Item 3", 7), "Value 3");
        directory.add(new HackedString("Item 4", 7), "Value 4");

        assertTrue(directory.contains(new HackedString("Item 3", 7)));

        MapEntry<HackedString, String>[] actuals = directory.getTable();
        assertNotNull(actuals[0]);
        assertFalse(actuals[0].isRemoved());
        assertEquals(4, directory.size());
    }

    @Test(timeout = TIMEOUT)
    public void test34ContainsEfficient() {
        directory.add(new HackedString("Item 1", 2), "Value 1");
        directory.add(new HackedString("Item 2", 2), "Value 2");
        directory.add(new HackedString("Item 3", 2), "Value 3");

        MapEntry<HackedString, String>[] actuals = directory.getTable();
        actuals[6] = new MapEntry<>(new HackedString("Item 4", 2), "Value 4");

        // This shouldn't get found, since it's after a null
        assertFalse(
            "Make sure you stop after hitting a null item.",
            directory.contains(new HackedString("Item 4", 2))
        );
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void test35ContainsNull() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");

        directory.contains(null);
    }

    @Test(timeout = TIMEOUT)
    public void test36ContainsNonexistent() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");

        assertFalse(directory.contains(new HackedString("Hash8 (again)", 8)));
    }

    @Test(timeout = TIMEOUT)
    public void test37ContainsNonexistentFull() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");
        directory.resizeBackingTable(4);

        assertFalse(directory.contains(new HackedString("Hash8 (again)", 8)));
    }

    @Test(timeout = TIMEOUT)
    public void test38ContainsTwice() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");

        assertTrue(directory.contains(new HackedString("Hash8", 8)));

        MapEntry<HackedString, String>[] actuals = directory.getTable();
        assertNotNull(actuals[8]);
        assertFalse(actuals[8].isRemoved());
        assertEquals(4, directory.size());

        assertTrue(directory.contains(new HackedString("Hash8", 8)));
    }

    @Test(timeout = TIMEOUT)
    public void test39ContainsEmpty() {
        // Just to make sure there's not a NullReferenceException
        assertFalse(directory.contains(new HackedString("Hash8", 8)));
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void test40ResizeTo0() {
        directory.resizeBackingTable(0);
    }

    @Test(timeout = TIMEOUT)
    public void test41ResizeTo1() {
        directory.add(new HackedString("Hash8", 8), "a");

        directory.resizeBackingTable(1);

        MapEntry<String, String>[]expected = (MapEntry<String, String>[]) new MapEntry[]{
                new MapEntry<>("Hash8", "a")
        };
        assertArrayEquals(expected, directory.getTable());
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void test42ResizeTooSmall() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");

        directory.resizeBackingTable(2);
    }

    @Test(timeout = TIMEOUT)
    public void test43Clear() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");

        assertEquals(4, directory.size());
        directory.resizeBackingTable(HashMap.STARTING_SIZE * 2);
        assertEquals(HashMap.STARTING_SIZE * 2, directory.getTable().length);

        directory.clear();

        MapEntry<String, String>[] expected =
                (MapEntry<String, String>[]) new MapEntry[HashMap.STARTING_SIZE];

        assertEquals(0, directory.size());
        assertEquals("Size was not reset to default", HashMap.STARTING_SIZE, directory.getTable().length);
        assertArrayEquals(expected, directory.getTable());
    }

    @Test(timeout = TIMEOUT)
    public void test44KeySet() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");

        Set<HackedString> keys = directory.keySet();
        assertEquals(4, keys.size());

        Object[] keysArr = keys.toArray();
        Arrays.sort(keysArr);

        assertArrayEquals(
                new HackedString[]{
                        new HackedString("Hash10", 8),
                        new HackedString("Hash4", 7),
                        new HackedString("Hash7", 4),
                        new HackedString("Hash8", 10)
                },
                keysArr
        );
    }

    @Test(timeout = TIMEOUT)
    public void test45KeySetEmpty() {
        assertArrayEquals(new HackedString[]{}, directory.keySet().toArray());
    }

    @Test(timeout = TIMEOUT)
    public void test46Values() {
        directory.add(new HackedString("Hash8", 8), "a");
        directory.add(new HackedString("Hash7", 7), "b");
        directory.add(new HackedString("Hash4", 4), "c");
        directory.add(new HackedString("Hash10", 10), "d");
        directory.add(new HackedString("Hash12", 10), "a");

        List<String> values = directory.values();
        assertEquals(5, values.size());

        Object[] valuesArr = values.toArray();
        Arrays.sort(valuesArr);

        assertArrayEquals(
                new String[]{
                        "a",
                        "a",
                        "b",
                        "c",
                        "d"
                },
                valuesArr
        );
    }

    @Test(timeout = TIMEOUT)
    public void test47KeySetEmpty() {
        assertArrayEquals(new String[]{}, directory.values().toArray());
    }

    private MapEntry removed(MapEntry entry){
        entry.setRemoved(true);
        return entry;
    }

    private static class HackedString implements Comparable<HackedString> {
        private String s;
        private int hashcode;

        /**
         * Create a wrapper object around a String object, for the purposes
         * of controlling the hash code.
         *
         * @param s        string to store in this object
         * @param hashcode the hashcode to return
         */
        public HackedString(String s, int hashcode) {
            this.s = s;
            this.hashcode = hashcode;
        }

        @Override
        public int hashCode() {
            return this.hashcode;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof HackedString) {
                return s.equals(((HackedString) o).s);
            }
            if (o instanceof String) {
                return s.equals(o);
            }
            return false;
        }

        /**
         * Stop highlighting "public" in read
         *
         * @param o The object to compare
         * @return Same thing a compareTo normally does
         */
        public int compareTo(HackedString o) {
            return s.compareTo(o.toString());
        }

        @Override
        public String toString() {
            return s;
        }
    }
}