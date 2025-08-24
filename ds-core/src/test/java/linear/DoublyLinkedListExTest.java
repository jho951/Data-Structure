package linear;

import linear.list.MyList;
import linear.list.linkedlist.internal.DoublyLinkedListEx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 양방향 연결 리스트 단위 테스트
 */
public class DoublyLinkedListExTest {

	private MyList<Integer> list;

	@BeforeEach
	void setUp() {
		list = new DoublyLinkedListEx<>();
	}

	@Test
	void testAddAndGet() {
		list.add(10);
		list.add(20);
		assertEquals(2, list.size());
		assertEquals(10, list.get(0));
		assertEquals(20, list.get(1));
	}

	@Test
	void testInsertAtIndex() {
		list.add(10);
		list.add(30);
		list.add(1, 20);
		assertEquals("[10, 20, 30]", list.toString());
	}

	@Test
	void testSet() {
		list.add(1);
		list.add(2);
		int old = list.set(1, 99);
		assertEquals(2, old);
		assertEquals(99, list.get(1));
	}

	@Test
	void testRemove() {
		list.add(1);
		list.add(2);
		list.add(3);
		int removed = list.remove(1);
		assertEquals(2, removed);
		assertEquals("[1, 3]", list.toString());
	}

	@Test
	void testClear() {
		list.add(1);
		list.add(2);
		list.clear();
		assertTrue(list.isEmpty());
	}

	@Test
	void testIterator() {
		list.add(1);
		list.add(2);
		list.add(3);
		StringBuilder sb = new StringBuilder();
		for (int val : list) sb.append(val);
		assertEquals("123", sb.toString());
	}
}
