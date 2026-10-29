package ru.nsu.oop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HashTableTest {
    private static final class CollisionKey {
        private final int value;

        private CollisionKey(int value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return -7;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof CollisionKey other && value == other.value;
        }
    }

    @Test
    void emptyTable() {
        HashTable<Integer, String> table = new HashTable<>();
        assertEquals(0, table.size());
        assertNull(table.get(1));
        assertFalse(table.containsKey(1));
        assertFalse(table.iterator().hasNext());
        assertEquals("{}", table.toString());
    }

    @Test
    void insertsDistinctKeysWithEqualValues() {
        HashTable<Integer, String> table = new HashTable<>();
        table.put(1, "A");
        table.put(17, "A");
        assertEquals(2, table.size());
        assertEquals("A", table.get(1));
        assertEquals("A", table.get(17));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 17, 33})
    void replacesAnyPositionInCollisionChain(int key) {
        HashTable<Integer, String> table = collisionTable();
        table.put(key, "changed");
        assertEquals(3, table.size());
        assertEquals("changed", table.get(key));
        for (int candidate : new int[]{1, 17, 33}) {
            if (candidate != key) {
                assertEquals("value" + candidate, table.get(candidate));
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 17, 33})
    void removesAnyPositionInCollisionChain(int key) {
        HashTable<Integer, String> table = collisionTable();
        table.remove(key);
        assertEquals(2, table.size());
        assertFalse(table.containsKey(key));
        assertNull(table.get(key));
        for (int candidate : new int[]{1, 17, 33}) {
            if (candidate != key) {
                assertEquals("value" + candidate, table.get(candidate));
            }
        }
    }

    @Test
    void removesOnlyNodeAndCanReuseBucket() {
        HashTable<Integer, String> table = new HashTable<>();
        table.put(1, "A");
        table.remove(1);
        assertEquals(0, table.size());
        assertFalse(table.iterator().hasNext());
        table.put(17, "B");
        assertEquals(1, table.size());
        assertEquals("B", table.get(17));
    }

    @Test
    void removingMissingKeyPreservesTableAndIterator() {
        HashTable<Integer, String> table = collisionTable();
        Iterator<Node<Integer, String>> iterator = table.iterator();
        table.remove(49);
        assertEquals(3, table.size());
        assertEquals(3, count(iterator));
    }

    @Test
    void updatesExistingKey() {
        HashTable<Integer, String> table = collisionTable();
        table.update(17, "new");
        assertEquals("new", table.get(17));
        assertEquals(3, table.size());
    }

    @Test
    void failedUpdatePreservesTableAndIterator() {
        HashTable<Integer, String> table = collisionTable();
        Iterator<Node<Integer, String>> iterator = table.iterator();
        assertThrows(IllegalArgumentException.class, () -> table.update(49, "new"));
        assertFalse(table.containsKey(49));
        assertEquals(3, table.size());
        assertEquals(3, count(iterator));
    }

    @Test
    void nullValueIsDifferentFromMissingKey() {
        HashTable<Integer, String> table = new HashTable<>();
        table.put(1, null);
        assertNull(table.get(1));
        assertTrue(table.containsKey(1));
        assertFalse(table.containsKey(17));
        assertNull(table.iterator().next().getValue());
        assertEquals("1 -> null", table.iterator().next().toString());
        assertEquals("{1=null}", table.toString());
        table.update(1, "new");
        assertEquals("new", table.get(1));
    }

    @Test
    void supportsNegativeAndMinimumIntegerHashes() {
        HashTable<Integer, String> table = new HashTable<>();
        table.put(-1, "negative");
        table.put(Integer.MIN_VALUE, "minimum");
        assertEquals("negative", table.get(-1));
        assertEquals("minimum", table.get(Integer.MIN_VALUE));
        table.expansion();
        assertEquals("minimum", table.get(Integer.MIN_VALUE));
        table.remove(-1);
        assertFalse(table.containsKey(-1));
    }

    @Test
    void repeatedAutomaticGrowthPreservesAllEntries() {
        HashTable<Integer, Integer> table = new HashTable<>();
        for (int i = 0; i < 1000; i++) {
            table.put(i, i * i);
        }
        assertEquals(1000, table.size());
        for (int i = 0; i < 1000; i++) {
            assertEquals(Integer.valueOf(i * i), table.get(i));
        }
        assertEquals(1000, snapshot(table).size());
    }

    @Test
    void growthPreservesCollisionsAndEqualKeyObjects() {
        HashTable<CollisionKey, Integer> table = new HashTable<>();
        for (int i = 0; i < 100; i++) {
            table.put(new CollisionKey(i), i);
        }
        for (int i = 0; i < 100; i++) {
            assertEquals(Integer.valueOf(i), table.get(new CollisionKey(i)));
        }
        table.update(new CollisionKey(50), 1000);
        table.remove(new CollisionKey(99));
        assertEquals(99, table.size());
        assertEquals(Integer.valueOf(1000), table.get(new CollisionKey(50)));
        assertFalse(table.containsKey(new CollisionKey(99)));
        assertEquals(99, snapshot(table).size());
    }

    @Test
    void forEachVisitsSparseBucketsAndWholeChains() {
        HashTable<Integer, String> table = new HashTable<>();
        table.put(15, "last");
        table.put(1, "first");
        table.put(17, "collision");
        assertEquals(Map.of(15, "last", 1, "first", 17, "collision"), snapshot(table));
    }

    @Test
    void repeatedHasNextDoesNotConsumeElement() {
        HashTable<Integer, String> table = new HashTable<>();
        table.put(15, "A");
        Iterator<Node<Integer, String>> iterator = table.iterator();
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(15), iterator.next().getKey());
        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNext());
    }

    @Test
    void iteratorsHaveIndependentPositions() {
        HashTable<Integer, String> table = collisionTable();
        Iterator<Node<Integer, String>> first = table.iterator();
        Iterator<Node<Integer, String>> second = table.iterator();
        Node<Integer, String> firstEntry = first.next();
        first.next();
        assertEquals(firstEntry.getKey(), second.next().getKey());
        assertEquals(1, count(first));
        assertEquals(2, count(second));
    }

    @Test
    void nextThrowsAfterExhaustionForEmptyAndNonemptyTable() {
        HashTable<Integer, String> table = new HashTable<>();
        Iterator<Node<Integer, String>> empty = table.iterator();
        assertThrows(NoSuchElementException.class, empty::next);
        table.put(1, "A");
        Iterator<Node<Integer, String>> iterator = table.iterator();
        iterator.next();
        assertThrows(NoSuchElementException.class, iterator::next);
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void insertionInvalidatesExistingIterators() {
        HashTable<Integer, String> table = collisionTable();
        Iterator<Node<Integer, String>> first = table.iterator();
        Iterator<Node<Integer, String>> second = table.iterator();
        table.put(49, "new");
        assertThrows(ConcurrentModificationException.class, first::hasNext);
        assertThrows(ConcurrentModificationException.class, second::next);
    }

    @Test
    void removalInvalidatesExistingIterator() {
        HashTable<Integer, String> table = collisionTable();
        Iterator<Node<Integer, String>> iterator = table.iterator();
        table.remove(17);
        assertThrows(ConcurrentModificationException.class, iterator::hasNext);
        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    void sizeReturningToPreviousValueDoesNotHideModification() {
        HashTable<Integer, String> table = collisionTable();
        Iterator<Node<Integer, String>> iterator = table.iterator();
        table.remove(17);
        table.put(49, "new");
        assertEquals(3, table.size());
        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    void updateDoesNotInvalidateIterator() {
        HashTable<Integer, String> table = new HashTable<>();
        table.put(1, "old");
        Iterator<Node<Integer, String>> iterator = table.iterator();
        table.update(1, "new");
        assertTrue(iterator.hasNext());
        assertEquals("new", iterator.next().getValue());
    }

    @Test
    void putReplacementDoesNotInvalidateIterator() {
        HashTable<Integer, String> table = collisionTable();
        Iterator<Node<Integer, String>> iterator = table.iterator();
        table.put(17, "new");
        assertEquals(3, count(iterator));
        assertEquals(3, table.size());
    }

    @Test
    void explicitExpansionInvalidatesIteratorAndPreservesEntries() {
        HashTable<Integer, String> table = collisionTable();
        Map<Integer, String> expected = snapshot(table);
        Iterator<Node<Integer, String>> iterator = table.iterator();
        table.expansion();
        assertThrows(ConcurrentModificationException.class, iterator::hasNext);
        assertThrows(ConcurrentModificationException.class, iterator::next);
        assertEquals(expected, snapshot(table));
        assertEquals(3, table.size());
    }

    @Test
    void returnedNodesCannotChangeTableLinksOrValues() {
        HashTable<Integer, String> table = collisionTable();
        Node<Integer, String> entry = table.iterator().next();
        Integer key = entry.getKey();
        String value = entry.getValue();
        assertNull(entry.getNext());
        entry.setValue("changed outside");
        entry.setNext(new Node<>(99, "extra", null));
        assertEquals(value, table.get(key));
        assertFalse(table.containsKey(99));
        assertEquals(3, snapshot(table).size());
    }

    @Test
    void iteratorRemoveIsUnsupported() {
        Iterator<Node<Integer, String>> iterator = collisionTable().iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    void equalityIgnoresInsertionOrderCapacityAndChains() {
        HashTable<CollisionKey, String> first = new HashTable<>();
        HashTable<CollisionKey, String> second = new HashTable<>();
        for (int i = 0; i < 20; i++) {
            first.put(new CollisionKey(i), i == 7 ? null : "value" + i);
        }
        for (int i = 19; i >= 0; i--) {
            second.put(new CollisionKey(i), i == 7 ? null : "value" + i);
        }
        second.expansion();
        assertEquals(first, first);
        assertEquals(first, second);
        assertEquals(second, first);
        assertEquals(first.hashCode(), second.hashCode());
        second.update(new CollisionKey(7), "different");
        assertNotEquals(first, second);
        second.remove(new CollisionKey(7));
        assertNotEquals(first, second);
    }

    @Test
    void equalityDistinguishesMissingKeysAndNullValues() {
        HashTable<Integer, String> first = new HashTable<>();
        HashTable<Integer, String> second = new HashTable<>();
        first.put(1, null);
        second.put(17, null);
        assertNotEquals(first, second);
        assertNotEquals(second, first);
    }

    @Test
    void equalityHandlesEmptyTablesDifferentTypesAndOtherObjects() {
        HashTable<Integer, String> first = new HashTable<>();
        HashTable<String, String> second = new HashTable<>();
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        first.put(1, "A");
        second.put("1", "A");
        assertNotEquals(first, second);
        assertNotEquals(first, null);
        assertNotEquals(first, "not a table");
    }

    @Test
    void toStringContainsAllPairsIncludingNullValue() {
        HashTable<Integer, String> table = collisionTable();
        table.put(7, null);
        String text = table.toString();
        assertTrue(text.startsWith("{"));
        assertTrue(text.endsWith("}"));
        assertTrue(text.contains("1=value1"));
        assertTrue(text.contains("17=value17"));
        assertTrue(text.contains("33=value33"));
        assertTrue(text.contains("7=null"));
    }

    @Test
    void nullKeysAreRejected() {
        HashTable<Integer, String> table = new HashTable<>();
        assertThrows(NullPointerException.class, () -> table.put(null, "A"));
        assertThrows(NullPointerException.class, () -> table.get(null));
        assertThrows(NullPointerException.class, () -> table.remove(null));
        assertThrows(NullPointerException.class, () -> table.containsKey(null));
        assertThrows(NullPointerException.class, () -> table.update(null, "A"));
        assertEquals(0, table.size());
    }

    @Test
    void randomizedOperationsMatchStandardMap() {
        HashTable<Integer, String> table = new HashTable<>();
        Map<Integer, String> expected = new HashMap<>();
        Random random = new Random(20261029);
        for (int i = 0; i < 5000; i++) {
            int key = i % 37 == 0 ? Integer.MIN_VALUE : random.nextInt(121) - 60;
            String value = i % 7 == 0 ? null : "value" + i;
            switch (random.nextInt(5)) {
                case 0:
                    table.put(key, value);
                    expected.put(key, value);
                    break;
                case 1:
                    table.remove(key);
                    expected.remove(key);
                    break;
                case 2:
                    if (expected.containsKey(key)) {
                        table.update(key, value);
                        expected.put(key, value);
                    } else {
                        assertThrows(IllegalArgumentException.class,
                                () -> table.update(key, value));
                    }
                    break;
                case 3:
                    assertEquals(expected.get(key), table.get(key));
                    break;
                default:
                    assertEquals(expected.containsKey(key), table.containsKey(key));
            }
            assertEquals(expected.size(), table.size());
        }
        assertEquals(expected, snapshot(table));
    }

    private static HashTable<Integer, String> collisionTable() {
        HashTable<Integer, String> table = new HashTable<>();
        table.put(1, "value1");
        table.put(17, "value17");
        table.put(33, "value33");
        return table;
    }

    private static int count(Iterator<?> iterator) {
        int result = 0;
        while (iterator.hasNext()) {
            iterator.next();
            result += 1;
        }
        return result;
    }

    private static <K, V> Map<K, V> snapshot(HashTable<K, V> table) {
        Map<K, V> result = new HashMap<>();
        for (Node<K, V> entry : table) {
            assertFalse(result.containsKey(entry.getKey()), "Итератор повторил ключ");
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
