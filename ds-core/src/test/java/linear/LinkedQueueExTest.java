package linear;

import linear.queue.MyQueue;
import linear.queue.Queues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LinkedQueueEx 단위 테스트
 * - 연결 리스트 기반 큐 (FIFO) 검증
 * - enqueue / dequeue / peek / clear
 * - 예외, fail-fast iterator, 대량/교차 시나리오
 */
public class LinkedQueueExTest {

	private MyQueue<Integer> q;

	@BeforeEach
	void setUp() {
		q = Queues.linked();
	}

	@Test
	void testEnqueueDequeueFifo() {
		q.enqueue(10);
		q.enqueue(20);
		q.enqueue(30);

		assertEquals(3, q.size());
		assertEquals(10, q.dequeue());
		assertEquals(20, q.dequeue());
		assertEquals(30, q.dequeue());
		assertTrue(q.isEmpty());
	}

	@Test
	void testPeek() {
		q.enqueue(99);
		assertEquals(99, q.peek());
		assertEquals(1, q.size(), "peek는 제거하지 않아야 함");
	}

	@Test
	void testDequeueOnEmptyThrows() {
		assertThrows(NoSuchElementException.class, () -> q.dequeue());
	}

	@Test
	void testPeekOnEmptyThrows() {
		assertThrows(NoSuchElementException.class, () -> q.peek());
	}

	@Test
	void testClear() {
		q.enqueue(1);
		q.enqueue(2);
		q.clear();
		assertTrue(q.isEmpty());
		assertEquals(0, q.size());
		assertThrows(NoSuchElementException.class, () -> q.peek());
	}

	@Test
	void testIteratorOrder() {
		q.enqueue(1);
		q.enqueue(2);
		q.enqueue(3);

		StringBuilder sb = new StringBuilder();
		for (int v : q) sb.append(v);
		assertEquals("123", sb.toString(), "FIFO 순서로 순회해야 함");
	}

	@Test
	void testIteratorFailFast() {
		q.enqueue(1);
		q.enqueue(2);
		var it = q.iterator();
		assertTrue(it.hasNext());
		q.enqueue(3); // 구조 변경 → fail-fast
		assertThrows(ConcurrentModificationException.class, it::next);
	}

	@Test
	void testLargeSequential() {
		int n = 5000;
		for (int i = 0; i < n; i++) q.enqueue(i);
		assertEquals(n, q.size());
		for (int i = 0; i < n; i++) assertEquals(i, q.dequeue());
		assertTrue(q.isEmpty());
	}

	@Test
	void testInterleavedOperations() {
		q.enqueue(1);          // [1]
		q.enqueue(2);          // [1,2]
		assertEquals(1, q.dequeue()); // [2]
		q.enqueue(3);          // [2,3]
		q.enqueue(4);          // [2,3,4]
		assertEquals(2, q.peek());
		assertEquals(2, q.dequeue()); // [3,4]
		assertEquals(3, q.dequeue()); // [4]
		assertEquals(4, q.dequeue()); // []
		assertTrue(q.isEmpty());
	}

	@Test
	void testToStringFormat() {
		q.enqueue(7);
		q.enqueue(8);
		assertEquals("[7, 8]", q.toString());
	}
}
