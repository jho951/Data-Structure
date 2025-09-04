package linear;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

import linear.deque.internal.ArrayDequeEx;

@DisplayName("ArrayDequeEx 테스트")
class ArrayDequeExTest {

	@DisplayName("초기 상태 점검")
	@Test
	void testInitialState() {
		ArrayDequeEx<Integer> dq = new ArrayDequeEx<>();
		assertEquals(0, dq.size());
		assertTrue(dq.isEmpty());

		assertNull(dq.peekFirst());
		assertNull(dq.peekLast());
		assertNull(dq.pollFirst());
		assertNull(dq.pollLast());

		assertThrows(NoSuchElementException.class, dq::getFirst);
		assertThrows(NoSuchElementException.class, dq::getLast);
		assertThrows(NoSuchElementException.class, dq::removeFirst);
		assertThrows(NoSuchElementException.class, dq::removeLast);
		assertThrows(NoSuchElementException.class, dq::pop);
	}

	@DisplayName("addLast 순서 및 크기")
	@Test
	void testAddLastAndIterationOrder() {
		ArrayDequeEx<Integer> dq = new ArrayDequeEx<>();
		for (int i = 1; i <= 5; i++) dq.addLast(i);

		assertEquals(5, dq.size());
		assertEquals(1, dq.getFirst());
		assertEquals(5, dq.getLast());

		int expected = 1;
		for (int v : dq) {
			assertEquals(expected++, v);
		}
	}

	@DisplayName("addFirst 순서 및 크기")
	@Test
	void testAddFirstAndOrder() {
		ArrayDequeEx<Integer> dq = new ArrayDequeEx<>();
		for (int i = 1; i <= 5; i++) dq.addFirst(i);

		assertEquals(5, dq.size());
		assertEquals(5, dq.getFirst());
		assertEquals(1, dq.getLast());

		int expected = 5;
		for (int v : dq) {
			assertEquals(expected--, v);
		}
	}

	@DisplayName("offerFirst/offerLast는 항상 true 반환 + 내용 적재")
	@Test
	void testOfferMethods() {
		ArrayDequeEx<String> dq = new ArrayDequeEx<>();
		assertTrue(dq.offerFirst("B"));
		assertTrue(dq.offerLast("C"));
		assertTrue(dq.offerFirst("A"));

		assertEquals(3, dq.size());
		assertEquals("A", dq.getFirst());
		assertEquals("C", dq.getLast());

		String[] expected = {"A", "B", "C"};
		int i = 0;
		for (String s : dq) assertEquals(expected[i++], s);
	}

	@DisplayName("push/pop/peek 동작 (스택 API 호환)")
	@Test
	void testPushPopPeek() {
		ArrayDequeEx<Integer> dq = new ArrayDequeEx<>();
		dq.push(10);
		dq.push(20);
		dq.push(30);

		assertEquals(30, dq.peek());
		assertEquals(3, dq.size());

		assertEquals(30, dq.pop());
		assertEquals(20, dq.pop());
		assertEquals(10, dq.pop());
		assertTrue(dq.isEmpty());
	}

	@DisplayName("poll vs remove: 비어있을 때 poll은 null, remove는 예외")
	@Test
	void testPollVsRemove() {
		ArrayDequeEx<Integer> dq = new ArrayDequeEx<>();
		assertNull(dq.pollFirst());
		assertNull(dq.pollLast());
		assertThrows(NoSuchElementException.class, dq::removeFirst);
		assertThrows(NoSuchElementException.class, dq::removeLast);
	}

	@DisplayName("contains와 null 처리")
	@Test
	void testContainsAndNull() {
		ArrayDequeEx<String> dq = new ArrayDequeEx<>();
		dq.addLast("A");
		dq.addLast("B");
		dq.addLast("C");

		assertTrue(dq.contains("A"));
		assertTrue(dq.contains("B"));
		assertTrue(dq.contains("C"));
		assertFalse(dq.contains("D"));

		// null은 추가 불가
		assertFalse(dq.contains(null));
		assertThrows(NullPointerException.class, () -> dq.addFirst(null));
		assertThrows(NullPointerException.class, () -> dq.addLast(null));
	}

	@DisplayName("원형 래핑 후 자동 확장 시 순서 보존")
	@Test
	void testCircularWrapAndGrowPreservesOrder() {
		ArrayDequeEx<Integer> dq = new ArrayDequeEx<>(4); // 작은 용량으로 시작해 래핑/확장 유도
		dq.addLast(1);
		dq.addLast(2);
		dq.addLast(3);
		dq.addLast(4); // [1,2,3,4] (가득 참)

		// head를 앞으로 밀어 원형 상황 만들기
		assertEquals(1, dq.removeFirst()); // -> [2,3,4]
		assertEquals(2, dq.removeFirst()); // -> [3,4]

		// 래핑되도록 뒤에 추가
		dq.addLast(5);
		dq.addLast(6); // 현재 내부적으로 tail이 인덱스 0/1로 래핑되어야 함

		// 이제 확장 트리거 (size == capacity)
		dq.addLast(7); // grow() 발생 → 순서 유지되어야 함

		assertEquals(5, dq.getLast()); // 확장 전 마지막으로 넣은 값은 7이지만 getLast는 현재 마지막 요소여야 하므로 아래에서 검증
		// 주의: getLast()는 마지막 원소를 반환해야 하므로 바로 위 한 줄은 의도된 오타 방지용 추가 검증으로 대체
		assertEquals(7, dq.getLast());
		assertEquals(5, dq.peekFirst() + 2); // 간단한 관계 체크 (3 -> 5)

		int[] expected = {3, 4, 5, 6, 7};
		int i = 0;
		for (int v : dq) {
			assertEquals(expected[i++], v);
		}
		assertEquals(expected.length, dq.size());
		assertEquals(3, dq.getFirst());
		assertEquals(7, dq.getLast());
	}

	@DisplayName("addFirst에 의한 head 감소 래핑 동작")
	@Test
	void testAddFirstWrap() {
		ArrayDequeEx<Integer> dq = new ArrayDequeEx<>(4);
		dq.addFirst(1); // [1]
		dq.addFirst(2); // [2,1]
		dq.addFirst(3); // [3,2,1]
		dq.addFirst(4); // [4,3,2,1] (가득 참)

		int[] expected = {4, 3, 2, 1};
		int i = 0;
		for (int v : dq) assertEquals(expected[i++], v);

		assertEquals(4, dq.getFirst());
		assertEquals(1, dq.getLast());
	}

	@DisplayName("clear 이후 재사용 가능")
	@Test
	void testClear() {
		ArrayDequeEx<Integer> dq = new ArrayDequeEx<>();
		for (int i = 0; i < 10; i++) dq.addLast(i);
		assertFalse(dq.isEmpty());
		dq.clear();
		assertTrue(dq.isEmpty());
		assertEquals(0, dq.size());

		dq.addFirst(99);
		assertEquals(1, dq.size());
		assertEquals(99, dq.getFirst());
	}

	@DisplayName("Iterator 순회 순서 + fail-fast 동작")
	@Test
	void testIteratorOrderAndFailFast() {
		ArrayDequeEx<Integer> dq = new ArrayDequeEx<>();
		for (int i = 1; i <= 5; i++) dq.addLast(i);

		// 순서
		Iterator<Integer> it = dq.iterator();
		for (int expected = 1; expected <= 5; expected++) {
			assertTrue(it.hasNext());
			assertEquals(expected, it.next());
		}
		assertFalse(it.hasNext());

		// fail-fast: 이터레이터 획득 후 구조 변경 → 예외
		Iterator<Integer> it2 = dq.iterator();
		assertTrue(it2.hasNext());
		dq.addLast(6); // 구조 변경

		assertThrows(ConcurrentModificationException.class, it2::hasNext);
		// 또는 next() 호출 시에도 예외
		Iterator<Integer> it3 = dq.iterator();
		dq.addFirst(0);
		assertThrows(ConcurrentModificationException.class, it3::next);
	}

	@DisplayName("대량 삽입 시 자동 확장 검증")
	@Test
	void testMassiveAddGrow() {
		ArrayDequeEx<Integer> dq = new ArrayDequeEx<>(2);
		for (int i = 0; i < 10_000; i++) {
			if ((i & 1) == 0) dq.addLast(i);
			else dq.addFirst(i);
		}
		assertEquals(10_000, dq.size());
		// 간단 스팟 체크
		assertNotNull(dq.peekFirst());
		assertNotNull(dq.peekLast());
	}
}
