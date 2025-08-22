package arraylist;

import java.util.Iterator;

/**
 * 최소한의 List 규약을 나타내는 인터페이스입니다. (제네릭/Iterable 지원)
 *
 * <p>설계 의도:
 * <ul>
 *   <li>외부에 공개되는 "계약(Contract)"을 명확히 분리해 내부 구현을 언제든 교체할 수 있게 합니다.</li>
 *   <li>구현체는 datastructure.internal 패키지에 두고, 외부에서는 MyList API만 사용하도록 유도합니다.</li>
 * </ul>
 *
 * <p>권장 복잡도(배열 기반 구현 기준):
 * <ul>
 *   <li>{@link #get(int)}: O(1)</li>
 *   <li>{@link #add(Object)}: 분할 상환(Amortized) O(1)</li>
 *   <li>{@link #add(int, Object)} / {@link #remove(int)}: O(n)</li>
 * </ul>
 *
 * @param <T> 원소 타입
 */
public interface MyList<T> extends Iterable<T> {

	/**
	 * 현재 리스트에 저장된 원소 수를 반환합니다.
	 * @return 원소 수
	 */
	int size();

	/**
	 * 리스트가 비어있는지 여부를 반환합니다.
	 * @return 비어있으면 true
	 */
	boolean isEmpty();

	/**
	 * 리스트의 끝에 원소를 추가합니다.
	 * 배열 기반 구현의 경우, 내부 용량이 부족하면 확장(ensureCapacity) 후 추가됩니다.
	 *
	 * @param value 추가할 값 (null 허용 여부는 구현 정책에 따름, 여기서는 허용)
	 */
	void add(T value);

	/**
	 * 지정한 인덱스 위치에 원소를 삽입합니다.
	 * 해당 인덱스부터의 원소들이 한 칸씩 뒤로 밀립니다.
	 *
	 * @param index 삽입할 위치 (0 이상, size 이하)
	 * @param value 삽입할 값
	 * @throws IndexOutOfBoundsException index < 0 또는 index > size 인 경우
	 */
	void add(int index, T value);

	/**
	 * 지정한 위치의 원소를 반환합니다.
	 *
	 * @param index 0 이상 size-1 이하
	 * @return 해당 위치의 원소
	 * @throws IndexOutOfBoundsException 인덱스가 범위를 벗어난 경우
	 */
	T get(int index);

	/**
	 * 지정한 위치의 원소를 새 값으로 교체하고, 교체 전 값을 반환합니다.
	 *
	 * @param index 0 이상 size-1 이하
	 * @param value 새 값
	 * @return 교체 전 값
	 * @throws IndexOutOfBoundsException 인덱스가 범위를 벗어난 경우
	 */
	T set(int index, T value);

	/**
	 * 지정한 위치의 원소를 제거하고, 제거된 값을 반환합니다.
	 * 해당 인덱스 뒤의 원소들이 한 칸씩 앞으로 당겨집니다.
	 *
	 * @param index 0 이상 size-1 이하
	 * @return 제거된 값
	 * @throws IndexOutOfBoundsException 인덱스가 범위를 벗어난 경우
	 */
	T remove(int index);

	/**
	 * 리스트의 모든 원소를 제거합니다.
	 * 배열 기반 구현에서는 내부 배열을 재사용하되, 사용 영역을 null로 초기화하여
	 * GC가 참조를 해제할 수 있도록 합니다.
	 */
	void clear();

	/**
	 * 리스트를 순회하기 위한 반복자를 반환합니다.
	 * 구현에 따라 fail-fast 정책(구조 변경 감지)을 적용할 수 있습니다.
	 */
	@Override
	Iterator<T> iterator();
}
