package arraylist;

import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ArrayListEx의 정상/경계/예외 동작을 검증하는 단위 테스트입니다.
 * - add/get/set/remove/clear: 기본 연산 검증
 * - bounds: 인덱스 유효성 검사
 * - iteratorFailFast: 순회 중 구조 변경 감지
 * - growth: 대량 추가 시 용량 확장 및 데이터 무결성 확인
 */
class ArrayListExTest {

	@Test
	void addGetInsertSetRemoveClear() {
		MyList<Integer> list = Lists.arrayList();
		assertTrue(list.isEmpty(), "초기에는 비어 있어야 함");

		list.add(1);
		list.add(2);
		list.add(1, 99); // [1, 99, 2]
		assertEquals(3, list.size());
		assertEquals(1, list.get(0));
		assertEquals(99, list.get(1));
		assertEquals(2, list.get(2));

		// set: 교체 전 값이 반환되어야 함
		assertEquals(99, list.set(1, 100)); // [1, 100, 2]
		assertEquals(100, list.get(1));

		// remove: 제거된 값이 반환되어야 함
		assertEquals(100, list.remove(1)); // [1, 2]
		assertEquals(2, list.size());

		// clear: 모두 삭제 후 비어 있어야 함
		list.clear();
		assertTrue(list.isEmpty());
	}

	@Test
	void bounds() {
		MyList<String> list = Lists.arrayList();
		// 빈 리스트에서 get은 범위 밖
		assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));

		list.add("a");
		assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
		// size=1 일 때 index=2 삽입은 범위 밖(허용 0~1)
		assertThrows(IndexOutOfBoundsException.class, () -> list.add(2, "x"));
		// remove(1)도 범위 밖
		assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
	}

	@Test
	void iteratorFailFast() {
		MyList<Integer> list = Lists.arrayList();
		list.add(1); list.add(2);

		var it = list.iterator();
		assertEquals(1, it.next());

		// 반복자 생성 후 리스트 구조 변경 → fail-fast 동작 확인
		list.add(3);
		assertThrows(ConcurrentModificationException.class, it::next);
	}

	@Test
	void growth() {
		MyList<Integer> list = Lists.arrayList(1);
		for (int i = 0; i < 1_000; i++) {
			list.add(i);
		}
		// 크기와 데이터 무결성 확인
		assertEquals(1_000, list.size());
		assertEquals(0, list.get(0));
		assertEquals(999, list.get(999));
	}
}
