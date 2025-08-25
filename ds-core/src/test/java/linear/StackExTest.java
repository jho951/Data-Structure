package linear;

import linear.stack.MyStack;
import linear.stack.internal.StackEx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Vector 기반 스택(StackEx) 단위 테스트
 */
public class StackExTest {

	private MyStack<Integer> stack;

	@BeforeEach
	void setUp() {
		stack = new StackEx<>();
	}

	@Test
	void testPushAndPeek() {
		stack.push(10);
		stack.push(20);
		assertEquals(2, stack.size());
		assertEquals(20, stack.peek());
		assertEquals(20, stack.peek(), "peek는 여러 번 호출해도 값이 같아야 함");
	}

	@Test
	void testPop() {
		stack.push(1);
		stack.push(2);
		stack.push(3);

		assertEquals(3, stack.pop());
		assertEquals(2, stack.pop());
		assertEquals(1, stack.pop());
		assertTrue(stack.isEmpty());
	}

	@Test
	void testPopOnEmptyThrowsException() {
		assertThrows(EmptyStackException.class, () -> stack.pop());
	}

	@Test
	void testPeekOnEmptyThrowsException() {
		assertThrows(EmptyStackException.class, () -> stack.peek());
	}

	@Test
	void testClear() {
		stack.push(100);
		stack.push(200);
		stack.clear();
		assertTrue(stack.isEmpty());
		assertEquals(0, stack.size());
	}

	@Test
	void testIterator() {
		stack.push(1);
		stack.push(2);
		stack.push(3);

		StringBuilder sb = new StringBuilder();
		for (int val : stack) {
			sb.append(val);
		}
		assertEquals("123", sb.toString()); // bottom→top 순서
	}

	@Test
	void testIteratorFailFast() {
		stack.push(1);
		stack.push(2);

		var it = stack.iterator();
		stack.push(3); // 구조 변경 발생

		assertThrows(ConcurrentModificationException.class, it::next);
	}
}
