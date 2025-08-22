package arraylist;

import arraylist.internal.ArrayListEx;

/**
 * 안정적인 생성 API(정적 팩토리 메서드)만 외부에 제공하는 유틸 클래스입니다.
 *
 * <p><strong>왜 필요한가?</strong>
 * <ul>
 *   <li>외부에서는 {@link MyList} 인터페이스만 알면 되고, 내부 구현 클래스명/패키지는 숨깁니다.</li>
 *   <li>성능/메모리 정책 변경, 구현 교체(예: 다른 배열 리스트) 시에도 외부 코드를 거의 건드리지 않습니다.</li>
 *   <li>일관된 생성 규칙(초기 용량 검증/정책)을 한 곳에서 관리할 수 있습니다.</li>
 * </ul>
 *
 * <p><strong>반환 타입</strong>은 항상 {@code MyList<T>}입니다.
 * 내부 구현체(현재는 {@code ArrayListEx})로 생성하되, 외부에는 노출하지 않는 것을 권장합니다.
 */
public final class Lists {
	/** 인스턴스화 금지(유틸 클래스) */
	private Lists() {}

	/**
	 * 기본 용량으로 배열 기반 리스트를 생성합니다.
	 *
	 * @param <T> 원소 타입
	 * @return {@code MyList<T>} 구현 인스턴스
	 */
	public static <T> MyList<T> arrayList() {
		return new ArrayListEx<>();
	}

	/**
	 * 지정한 초기 용량으로 배열 기반 리스트를 생성합니다.
	 *
	 * @param <T> 원소 타입
	 * @param capacity 초기 용량(0 이상). 내부 구현에서 최소 용량 이상으로 보정될 수 있습니다.
	 * @return {@code MyList<T>} 구현 인스턴스
	 * @throws IllegalArgumentException 음수 용량인 경우
	 */
	public static <T> MyList<T> arrayList(int capacity) {
		return new ArrayListEx<>(capacity);
	}
}
