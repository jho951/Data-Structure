package linear;

import linear.queue.MyQueue;
import linear.queue.Queues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * QueueEx 단위 테스트
 * - enqueue / dequeue / peek / clear 검증
 * - 예외 케이스 (빈 큐에서 dequeue/peek, iterator fail-fast)
 * - 대량 데이터 일관성(size/순서)
 */
public class ArrayQueueExTest {

	private MyQueue<Integer> queue;

	@BeforeEach
	void setUp() {
		queue = Queues.array();
	}

	@Test
	void testEnqueueAndDequeue() {
		queue.enqueue(10);
		queue.enqueue(20);
		queue.enqueue(30);

		assertEquals(3, queue.size());
		assertEquals(10, queue.dequeue());
		assertEquals(20, queue.dequeue());
		assertEquals(30, queue.dequeue());
		assertTrue(queue.isEmpty());
	}

	@Test
	void testPeek() {
		queue.enqueue(99);
		assertEquals(99, queue.peek());
		assertEquals(1, queue.size()); // peek은 제거하지 않음
	}

	@Test
	void testDequeueOnEmptyThrows() {
		assertThrows(NoSuchElementException.class, () -> queue.dequeue());
	}

	@Test
	void testPeekOnEmptyThrows() {
		assertThrows(NoSuchElementException.class, () -> queue.peek());
	}

	@Test
	void testClear() {
		queue.enqueue(1);
		queue.enqueue(2);
		queue.clear();
		assertTrue(queue.isEmpty());
		assertEquals(0, queue.size());
	}

	@Test
	void testIterator() {
		queue.enqueue(1);
		queue.enqueue(2);
		queue.enqueue(3);

		int sum = 0;
		for (int v : queue) sum += v;
		assertEquals(6, sum);
	}

	@Test
	void testIteratorFailFast() {
		queue.enqueue(1);
		queue.enqueue(2);

		var it = queue.iterator();
		assertTrue(it.hasNext());
		queue.enqueue(3); // 구조 변경 → fail-fast

		assertThrows(ConcurrentModificationException.class, it::next);
	}

	@Test
	void testLargeEnqueueDequeue() {
		int n = 1000;
		for (int i = 0; i < n; i++) queue.enqueue(i);
		assertEquals(n, queue.size());

		for (int i = 0; i < n; i++) {
			assertEquals(i, queue.dequeue());
		}
		assertTrue(queue.isEmpty());
	}
}