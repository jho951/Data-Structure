package list.arraylist.internal;

import list.MyList;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

/**
 * 동적 배열 기반의 리스트 구현체입니다. (스레드-세이프하지 않음)
 *
 * <h3>특징</h3>
 * <ul>
 *   <li><b>성장 정책</b>: 내부 용량(capacity)이 부족해지면 1.5배로 확장합니다.</li>
 *   <li><b>fail-fast 반복자</b>: 반복자 생성 이후 리스트가 구조적으로 변경되면
 *       {@link ConcurrentModificationException}을 던집니다.</li>
 *   <li><b>메모리 누수 방지</b>: 제거/초기화 시 사용 구간을 null로 채워 GC가 참조를 해제하도록 합니다.</li>
 * </ul>
 *
 * <h3>시간 복잡도(권장/평균)</h3>
 * <ul>
 *   <li>{@code get(i)}: O(1)</li>
 *   <li>{@code add(e)}: 분할 상환(Amortized) O(1)</li>
 *   <li>{@code add(i, e)} / {@code remove(i)}: O(n) — 중간 삽입/삭제 시 밀기/당기기 비용</li>
 * </ul>
 *
 * @param <T> 원소 타입
 */
public final class ArrayListEx<T> implements MyList<T> {
	/** 초기 용량 */
	private static final int DEFAULT_CAPACITY = 10;

	/** 실제 데이터를 담는 배열(용량 = elements.length) */
	private Object[] elements;

	/** 현재 원소 수(size). 유효 인덱스 범위는 [0, size) */
	private int size;

	/**
	 * 구조적 변경(추가/삭제/대량 clear 등) 발생 시 1 증가.
	 * fail-fast 반복자가 생성 시점의 값과 현재 값을 비교하여 변경을 감지합니다.
	 */
	private int modCount;

	/**
	 * 초기 용량을 제공하지 않으면, DEFAULT_CAPACITY로 리스트를 생성합니다.
	 * 기본 용량으로 리스트를 생성합니다.
	 */
	public ArrayListEx() {
		this(DEFAULT_CAPACITY);
	}

	/**
	 * 주어진 초기 용량으로 리스트를 생성합니다.
	 * 너무 적은 용량 시 확장 비용이 잦게 발생하므로, DEFAULT_CAPACITY 이상으로 적용합니다.
	 *
	 * @param initialCapacity 0 이상
	 * @throws IllegalArgumentException 음수 용량 전달 시
	 */
	public ArrayListEx(int initialCapacity) {
		if (initialCapacity < 0) throw new IllegalArgumentException("초기 용량은 0 이상이여야 합니다.");
		elements = new Object[Math.max(DEFAULT_CAPACITY, initialCapacity)];
	}

	/** {@inheritDoc} */
	@Override
	public int size() { return size; }

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() { return size == 0; }

	/**
	 * 리스트의 끝 쪽에 value를 추가합니다.
	 * 1) 용량 확인/확장 → 2) tail 위치에 값 대입 → 3) size/modCount 증가
	 *
	 * @param value 리스트에 추가될 원소
	 */
	@Override
	public void add(T value) {
		ensureCapacity(size + 1);
		elements[size++] = value;
		modCount++;
	}

	/**
	 * 지정 인덱스에 원소를 삽입합니다.
	 * 1) 범위 체크(0 ~ size) → 2) 용량 확인/확장 → 3) 우측 블록을 한 칸 뒤로 민 뒤 값 대입
	 */
	@Override
	public void add(int index, T value) {
		rangeForAdd(index);
		ensureCapacity(size + 1);
		System.arraycopy(elements, index, elements, index + 1, size - index);
		elements[index] = value;
		size++;
		modCount++;
	}

	/**
	 * 지정 인덱스의 원소를 반환합니다.
	 *
	 * @throws IndexOutOfBoundsException 인덱스가 범위를 벗어난 경우(0 <= index < size)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T get(int index) {
		range(index);
		return (T) elements[index];
	}

	/**
	 * 지정 인덱스의 원소를 새 값으로 교체하고, 교체 전 값을 반환합니다.
	 *
	 * @throws IndexOutOfBoundsException 인덱스가 범위를 벗어난 경우(0 <= index < size)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T set(int index, T value) {
		range(index);
		T old = (T) elements[index];
		elements[index] = value;
		return old;
	}

	/**
	 * 지정 인덱스의 원소를 제거하고, 제거된 값을 반환합니다.
	 * 우측 블록을 한 칸 앞으로 당긴 뒤, 마지막 칸을 null로 설정하여 메모리 누수(로이터링)를 방지합니다.
	 *
	 * @throws IndexOutOfBoundsException 인덱스가 범위를 벗어난 경우(0 <= index < size)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T remove(int index) {
		range(index);
		T old = (T) elements[index];

		int move = size - index - 1;
		if (move > 0) {
			System.arraycopy(elements, index + 1, elements, index, move);
		}

		elements[--size] = null; // 마지막 칸 비우기 → GC가 참조 해제 가능
		modCount++;
		return old;
	}

	/**
	 * 모든 원소를 제거합니다.
	 * 내부 배열을 재사용하되, 사용 중이던 구간만 null로 채워 GC가 수거하도록 합니다.
	 */
	@Override
	public void clear() {
		Arrays.fill(elements, 0, size, null);
		size = 0;
		modCount++;
	}

	/**
	 * 리스트를 순회하기 위한 반복자를 반환합니다.
	 * 반복자 생성 시점의 {@code modCount}를 스냅샷으로 저장하고,
	 * 순회 도중 리스트가 구조적으로 변경되면 {@link ConcurrentModificationException}을 던집니다.
	 * cursor 다음에 반환할 인덱스
	 */
	@Override
	public Iterator<T> iterator() {
		// 생성 시점의 수정 횟수
		final int expected = modCount;
		return new Iterator<T>() {
			int cursor = 0;

			@Override public boolean hasNext() { return cursor < size; }

			@SuppressWarnings("unchecked")
			@Override public T next() {
				if (expected != modCount) throw new ConcurrentModificationException();
				if (!hasNext()) throw new NoSuchElementException();
				return (T) elements[cursor++];
			}
		};
	}

	/* ---------- 내부 유틸리티 ---------- */

	/**
	 * 필요한 최소 용량(min)보다 현재 용량이 작으면 1.5배로 확장합니다.
	 * (단, min이 더 크면 min으로 맞춥니다.)
	 * 확장은 발생 시점에는 비용이 크지만, 분할 상환 관점에서 {@code add(e)}의 평균 복잡도는 O(1)을 유지합니다.
	 */
	private void ensureCapacity(int min) {
		if (min <= elements.length) return;
		int newCap = Math.max(elements.length + (elements.length >> 1), min);
		elements = Arrays.copyOf(elements, newCap);
	}

	/** 읽기/쓰기 공용 범위 체크: 0 <= index < size */
	private void range(int i) {
		if (i < 0 || i >= size) {
			throw new IndexOutOfBoundsException("index=" + i + ", size=" + size);
		}
	}

	/** 삽입 전용 범위 체크: 0 <= index <= size (size 위치 삽입 허용) */
	private void rangeForAdd(int i) {
		if (i < 0 || i > size) {
			throw new IndexOutOfBoundsException("index=" + i + ", size=" + size);
		}
	}
}
