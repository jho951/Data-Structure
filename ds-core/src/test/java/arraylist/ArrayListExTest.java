package arraylist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ArrayListEx의 정상/경계/예외 동작을 검증하는 단위 테스트 모음입니다.
 *
 * <h2>테스트 범주</h2>
 * <ul>
 *   <li><b>기본 연산</b>: add / add(index, e) / get / set / remove / clear</li>
 *   <li><b>경계값/예외</b>: 빈 리스트에서 get/remove, 음수/범위 초과 인덱스, 삽입 가능한 경계(0, size)</li>
 *   <li><b>반복자 일관성</b>: fail-fast 정책(반복 중 구조 변화 감지)</li>
 *   <li><b>성장 정책</b>: 대량 추가 시 용량 확장 및 데이터 무결성 확인</li>
 * </ul>
 *
 * <p><strong>검증 철학</strong>:
 * <ul>
 *   <li>행위 기반(입력/조작) → 결과/예외 기반(상태/반환/예외)으로 확인합니다.</li>
 *   <li>정상 케이스, 경계 케이스, 오류 케이스를 모두 포함합니다.</li>
 * </ul>
 */
class ArrayListExTest {

	/**
	 * <h3>목표</h3>
	 * <ul>
	 *   <li>리스트 초기 상태 확인(isEmpty)</li>
	 *   <li>append add, 중간 삽입 add(index, e)의 결과 인덱스 상태</li>
	 *   <li>set이 <i>교체 전 값</i>을 반환하는지</li>
	 *   <li>remove가 <i>제거된 값</i>을 반환하고 size가 줄어드는지</li>
	 *   <li>clear 후 완전 비움 상태</li>
	 * </ul>
	 */
	@DisplayName("기본 연산: add / add(index) / get / set / remove / clear")
	@Test
	void addGetInsertSetRemoveClear() {
		// Given: 비어있는 리스트
		MyList<Integer> list = Lists.arrayList();
		assertTrue(list.isEmpty(), "초기에는 비어 있어야 함");

		// When: 맨 뒤에 1, 2 추가 후, 인덱스 1에 99 삽입 → [1, 99, 2]
		list.add(1);
		list.add(2);
		list.add(1, 99);

		// Then: 크기/순서 확인
		assertEquals(3, list.size());
		assertEquals(1, list.get(0));
		assertEquals(99, list.get(1));
		assertEquals(2, list.get(2));

		// And: set은 교체 전 값을 반환하고, 새 값이 반영되어야 함 → [1, 100, 2]
		assertEquals(99, list.set(1, 100));
		assertEquals(100, list.get(1));

		// And: remove는 제거한 값을 반환하고, 크기가 하나 줄어야 함 → [1, 2]
		assertEquals(100, list.remove(1));
		assertEquals(2, list.size());

		// And: clear는 모든 요소 제거
		list.clear();
		assertTrue(list.isEmpty());
	}

	/**
	 * <h3>목표</h3>
	 * <ul>
	 *   <li>빈 리스트에서 get은 예외가 발생해야 함</li>
	 *   <li>음수 인덱스 접근 예외</li>
	 *   <li>삽입 허용 경계: 0 ≤ index ≤ size (size는 tail 삽입 허용), 그 외는 예외</li>
	 *   <li>remove의 유효 범위: 0 ≤ index &lt; size</li>
	 * </ul>
	 */
	@DisplayName("경계/예외: 인덱스 범위 체크(get/add/remove)")
	@Test
	void bounds() {
		MyList<String> list = Lists.arrayList();

		// 빈 리스트에서 get(0)은 범위 밖 → 예외
		assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));

		list.add("a"); // size == 1

		// 음수 인덱스는 항상 예외
		assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));

		// add(index, e)에서 index == size는 tail 삽입이므로 허용이지만,
		// 여기서는 index == 2, size == 1 이므로 범위 밖 → 예외
		assertThrows(IndexOutOfBoundsException.class, () -> list.add(2, "x"));

		// remove의 유효 범위는 [0, size). size == 1에서 remove(1)은 범위 밖 → 예외
		assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
	}

	/**
	 * <h3>목표</h3>
	 * <ul>
	 *   <li>반복자 생성 이후 리스트가 구조적으로 변경되면(예: add/remove) fail-fast 발생</li>
	 *   <li>ConcurrentModificationException이 즉시 터지는지 확인</li>
	 * </ul>
	 *
	 * <p>설명: 반복자는 생성 시점의 수정 카운트(modCount)를 스냅샷으로 저장합니다.
	 * 순회 도중 add/remove로 modCount가 변경되면, 다음 next()에서 불일치를 감지해 예외가 납니다.</p>
	 */
	@DisplayName("반복자 fail-fast: 순회 중 구조 변경 감지")
	@Test
	void iteratorFailFast() {
		MyList<Integer> list = Lists.arrayList();
		list.add(1);
		list.add(2);

		var it = list.iterator();
		assertEquals(1, it.next()); // 첫 원소까지는 정상 순회

		// 여기서 리스트 구조 변경 → 다음 next()에서 fail-fast 동작해야 함
		list.add(3);

		// next() 호출 시 ConcurrentModificationException 기대
		assertThrows(ConcurrentModificationException.class, it::next);
	}

	/**
	 * <h3>목표</h3>
	 * <ul>
	 *   <li>대량 추가 시 내부 용량 확장이 정상 동작하는지</li>
	 *   <li>추가 후 데이터 무결성(인덱스-값 대응)이 유지되는지</li>
	 * </ul>
	 *
	 * <p>설명: ensureCapacity는 필요 시 1.5배 확장을 수행합니다.
	 * 확장 시점에는 비용이 크지만, 분할 상환 관점에서 add(e)의 평균 복잡도는 O(1)에 가깝게 유지됩니다.</p>
	 */
	@DisplayName("성장 정책: 대량 추가 시 확장 & 데이터 무결성")
	@Test
	void growth() {
		MyList<Integer> list = Lists.arrayList(1); // 아주 작은 초기 용량으로 시작

		// 0 ~ 999까지 1000개 추가 → 여러 번 확장될 것
		for (int i = 0; i < 1_000; i++) {
			list.add(i);
		}

		// 크기 및 앞/뒤 데이터 무결성 확인
		assertEquals(1_000, list.size());
		assertEquals(0, list.get(0));
		assertEquals(999, list.get(999));
	}
}
