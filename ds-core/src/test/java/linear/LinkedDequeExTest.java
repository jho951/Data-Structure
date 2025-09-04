package linear;

import linear.deque.MyDeque;
import linear.deque.internal.LinkedDequeEx;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LinkedDequeEx 테스트")
class LinkedDequeExTest {

	// ---- 기초 상태 ----
	@DisplayName("초기 상태: size=0, isEmpty=true, peek/poll는 null, get/remove는 예외")
	@Test
	void initialState() {
		MyDeque<Integer> dq = new LinkedDequeEx<>();
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

	// ---- 삽입/순서 ----
	@DisplayName("addLast: FIFO 순서 보장")
	@Test
	void addLastOrder() {
		MyDeque<Integer> dq = new LinkedDequeEx<>();
		for (int i = 1; i <= 5; i++) dq.addLast(i);

		assertEquals(5, dq.size());
		assertEquals(1, dq.getFirst());
		assertEquals(5, dq.getLast());

		int expected = 1;
		for (int v : dq) assertEquals(expected++, v);
	}

	@DisplayName("addFirst: LIFO 순서 보장(앞쪽 삽입)")
	@Test
	void addFirstOrder() {
		MyDeque<Integer> dq = new LinkedDequeEx<>();
		for (int i = 1; i <= 5; i++) dq.addFirst(i);

		assertEquals(5, dq.size());
		assertEquals(5, dq.getFirst());
		assertEquals(1, dq.getLast());

		int expected = 5;
		for (int v : dq) assertEquals(expected--, v);
	}

	@DisplayName("offerFirst/offerLast는 true를 반환하고 정상 적재")
	@Test
	void offerMethods() {
		MyDeque<String> dq = new LinkedDequeEx<>();
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

	// ---- 스택 API 호환 ----
	@DisplayName("push/pop/peek: 스택 API 일관성")
	@Test
	void pushPopPeek() {
		MyDeque<Integer> dq = new LinkedDequeEx<>();
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

	// ---- poll vs remove 규약 ----
	@DisplayName("poll은 null, remove는 예외")
	@Test
	void pollVsRemove() {
		MyDeque<Integer> dq = new LinkedDequeEx<>();
		assertNull(dq.pollFirst());
		assertNull(dq.pollLast());
		assertThrows(NoSuchElementException.class, dq::removeFirst);
		assertThrows(NoSuchElementException.class, dq::removeLast);
	}

	// ---- contains / null 정책 ----
	@DisplayName("contains 정확성 + null 금지 정책")
	@Test
	void containsAndNullPolicy() {
		MyDeque<String> dq = new LinkedDequeEx<>();
		dq.addLast("A");
		dq.addLast("B");
		dq.addLast("C");

		assertTrue(dq.contains("A"));
		assertTrue(dq.contains("B"));
		assertTrue(dq.contains("C"));
		assertFalse(dq.contains("D"));

		// null 금지 가정 (ArrayDequeEx와 정책 통일)
		assertFalse(dq.contains(null));
		assertThrows(NullPointerException.class, () -> dq.addFirst(null));
		assertThrows(NullPointerException.class, () -> dq.addLast(null));
	}

	// ---- clear / 재사용 ----
	@DisplayName("clear 후 재사용 가능하며 메서드 계약 유지")
	@Test
	void clearAndReuse() {
		MyDeque<Integer> dq = new LinkedDequeEx<>();
		for (int i = 0; i < 10; i++) dq.addLast(i);
		assertFalse(dq.isEmpty());

		dq.clear();
		assertTrue(dq.isEmpty());
		assertEquals(0, dq.size());
		assertNull(dq.peekFirst());
		assertNull(dq.peekLast());

		dq.addFirst(99);
		assertEquals(1, dq.size());
		assertEquals(99, dq.getFirst());
	}

	// ---- 반복자 규약 / fail-fast ----
	@DisplayName("반복자 순서 + fail-fast: hasNext/next 모두에서 감지")
	@Test
	void iteratorOrderAndFailFast() {
		MyDeque<Integer> dq = new LinkedDequeEx<>();
		for (int i = 1; i <= 5; i++) dq.addLast(i);

		// 순서 검증
		Iterator<Integer> it = dq.iterator();
		for (int expected = 1; expected <= 5; expected++) {
			assertTrue(it.hasNext());
			assertEquals(expected, it.next());
		}
		assertFalse(it.hasNext());

		// fail-fast: 이터레이터 생성 후 구조 변경 → CME
		Iterator<Integer> it2 = dq.iterator();
		assertTrue(it2.hasNext());
		dq.addLast(6); // 구조 변경

		assertThrows(ConcurrentModificationException.class, it2::hasNext);

		Iterator<Integer> it3 = dq.iterator();
		dq.addFirst(0);
		assertThrows(ConcurrentModificationException.class, it3::next);
	}

	// ---- 양끝 제거 일관성 ----
	@DisplayName("양끝 제거 시나리오: addFirst/addLast 혼합 후 remove/poll 일관성")
	@Test
	void removeBothEndsConsistency() {
		MyDeque<Integer> dq = new LinkedDequeEx<>();
		dq.addFirst(2);  // [2]
		dq.addLast(3);   // [2,3]
		dq.addFirst(1);  // [1,2,3]
		dq.addLast(4);   // [1,2,3,4]

		assertEquals(1, dq.removeFirst()); // [2,3,4]
		assertEquals(4, dq.removeLast());  // [2,3]
		assertEquals(2, dq.peekFirst());
		assertEquals(3, dq.peekLast());

		assertEquals(2, dq.pollFirst());   // [3]
		assertEquals(3, dq.pollLast());    // []
		assertTrue(dq.isEmpty());
	}

	// ---- 레퍼런스 대조(표준 ArrayDeque와 교차검증) ----
	@DisplayName("레퍼런스 대조: JDK ArrayDeque와 동작 동등성(랜덤 시나리오)")
	@Test
	void crossCheckWithJdkArrayDeque() {
		MyDeque<Integer> mine = new LinkedDequeEx<>();
		java.util.ArrayDeque<Integer> ref = new java.util.ArrayDeque<>();

		Random rnd = new Random(12345);
		for (int i = 0; i < 5000; i++) {
			int op = rnd.nextInt(10);
			switch (op) {
				case 0 -> { // addFirst
					int v = rnd.nextInt(1000);
					mine.addFirst(v); ref.addFirst(v);
				}
				case 1 -> { // addLast
					int v = rnd.nextInt(1000);
					mine.addLast(v); ref.addLast(v);
				}
				case 2 -> assertEquals(ref.pollFirst(), mine.pollFirst());
				case 3 -> assertEquals(ref.pollLast(), mine.pollLast());
				case 4 -> { // removeFirst 예외/반환 대조
					try {
						Integer m = mine.removeFirst();
						assertEquals(ref.removeFirst(), m);
					} catch (NoSuchElementException e) {
						assertThrows(NoSuchElementException.class, ref::removeFirst);
					}
				}
				case 5 -> { // removeLast 예외/반환 대조
					try {
						Integer m = mine.removeLast();
						assertEquals(ref.removeLast(), m);
					} catch (NoSuchElementException e) {
						assertThrows(NoSuchElementException.class, ref::removeLast);
					}
				}
				case 6 -> assertEquals(ref.peekFirst(), mine.peekFirst());
				case 7 -> assertEquals(ref.peekLast(), mine.peekLast());
				case 8 -> { // contains 스팟 체크
					int probe = rnd.nextInt(1000);
					assertEquals(ref.contains(probe), mine.contains(probe));
				}
				case 9 -> { // clear 멱등성 스팟
					if (rnd.nextDouble() < 0.02) {
						mine.clear(); ref.clear();
						assertEquals(0, mine.size());
						assertTrue(mine.isEmpty());
					}
				}
			}

			// 공통 상태 단정
			assertEquals(ref.size(), mine.size());
			assertEquals(ref.isEmpty(), mine.isEmpty());

			// 순서 동치(Deque는 순서가 정의됨)
			assertEquals(toList(ref.iterator()), toList(mine.iterator()));
		}
	}

	// ---- 헬퍼 ----
	private static <T> java.util.List<T> toList(Iterator<T> it) {
		var out = new java.util.ArrayList<T>();
		while (it.hasNext()) out.add(it.next());
		return out;
	}
}
