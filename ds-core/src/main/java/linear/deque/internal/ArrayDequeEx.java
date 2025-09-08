package linear.deque.internal;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import linear.deque.MyDeque;

/**
 * <h1>배열 기반 Deque (원형 버퍼 + 자동 확장)</h1>
 *
 * <p>고정 크기 배열을 원형(circular)으로 사용해 앞/뒤 양쪽 끝에서
 * 원소의 삽입/삭제를 효율적으로 수행하는 Deque 구현입니다.
 * 용량이 가득 차면 내부 배열을 <b>2배로 확장</b>하며,
 * 이 때 요소의 논리적 순서를 보존합니다.</p>
 *
 * <h2>특징</h2>
 * <ul>
 *   <li><b>null 금지</b>: {@code addFirst}/{@code addLast}에 {@code null}을 넣으면 {@link NullPointerException}</li>
 *   <li><b>시간 복잡도</b>: 앞/뒤 삽입/삭제는 분할상환(Amortized) <b>O(1)</b></li>
 *   <li><b>자동 확장</b>: 용량 초과 시 내부 배열을 <b>2배</b>로 확장</li>
 *   <li><b>Fail-fast Iterator</b>: 순회 도중 구조 변경 시 {@link ConcurrentModificationException}</li>
 *   <li>스레드-안전성: <b>비동기 안전 아님</b></li>
 * </ul>
 *
 * <h2>불변식(Invariants)</h2>
 * <ul>
 *   <li>{@code head}는 다음 {@code removeFirst/peekFirst} 위치</li>
 *   <li>{@code tail}은 다음 {@code addLast/offerLast} 위치(미사용 칸)</li>
 *   <li>유효 요소 수 {@code size}는 {@code 0 <= size <= elements.length}</li>
 * </ul>
 *
 * <h2>예시</h2>
 * <pre>{@code
 * MyDeque<Integer> dq = new ArrayDequeEx<>();
 * dq.addFirst(2);
 * dq.addLast(3);
 * dq.push(1);          // addFirst와 동일
 * int first = dq.getFirst(); // 1
 * int last  = dq.getLast();  // 3
 * dq.pop();            // 1 제거
 * }</pre>
 *
 * @param <T> 원소 타입(Null 불가)
 * @since 1.0
 */
public final class ArrayDequeEx<T> implements MyDeque<T> {

	private Object[] elements;
	private int head;   // 다음 removeFirst/peekFirst 위치
	private int tail;   // 다음 addLast/offerLast 위치
	private int size;
	private int modCount;

	private static final int DEFAULT_CAP = 8;

	/**
	 * 기본 용량(8)으로 비어 있는 Deque를 생성합니다.
	 */
	public ArrayDequeEx() {
		this(DEFAULT_CAP);
	}

	/**
	 * 지정한 초기 용량으로 비어 있는 Deque를 생성합니다.
	 * 초기 용량이 1 미만이면 기본 용량(8)로 보정됩니다.
	 *
	 * @param initialCapacity 초기 용량(최소 1)
	 */
	public ArrayDequeEx(int initialCapacity) {
		if (initialCapacity < 1) initialCapacity = DEFAULT_CAP;
		// 2의 거듭제곱 정규화는 하지 않고 모듈러 연산으로 원형 인덱싱을 처리
		elements = new Object[initialCapacity];
	}

	// ============ MyDeque API ============

	/**
	 * 현재 저장된 요소의 수를 반환합니다.
	 *
	 * @return 요소 수
	 * @implNote O(1)
	 */
	@Override public int size() { return size; }

	/**
	 * Deque가 비어 있는지 반환합니다.
	 *
	 * @return 비어 있으면 {@code true}
	 * @implNote O(1)
	 */
	@Override public boolean isEmpty() { return size == 0; }

	/**
	 * 앞쪽(head 앞)에 요소를 추가합니다.
	 *
	 * @param value 추가할 값(Null 불가)
	 * @throws NullPointerException 값이 {@code null}인 경우
	 * @implNote 분할상환 O(1). 필요 시 내부 배열이 2배로 확장됩니다.
	 */
	@Override
	public void addFirst(T value) {
		if (value == null) throw new NullPointerException("null not allowed");
		ensureCapacityForOneMore();
		head = dec(head);
		elements[head] = value;
		size++; modCount++;
	}

	/**
	 * {@link #addFirst(Object)}와 동일하며, 항상 {@code true}를 반환합니다.
	 * (용량은 자동 확장됩니다)
	 *
	 * @param value 추가할 값(Null 불가)
	 * @return 항상 {@code true}
	 * @throws NullPointerException 값이 {@code null}인 경우
	 */
	@Override
	public boolean offerFirst(T value) {
		addFirst(value);
		return true;
	}

	/**
	 * 뒤쪽(tail 위치)에 요소를 추가합니다.
	 *
	 * @param value 추가할 값(Null 불가)
	 * @throws NullPointerException 값이 {@code null}인 경우
	 * @implNote 분할상환 O(1). 필요 시 내부 배열이 2배로 확장됩니다.
	 */
	@Override
	public void addLast(T value) {
		if (value == null) throw new NullPointerException("null not allowed");
		ensureCapacityForOneMore();
		elements[tail] = value;
		tail = inc(tail);
		size++; modCount++;
	}

	/**
	 * {@link #addLast(Object)}와 동일하며, 항상 {@code true}를 반환합니다.
	 * (용량은 자동 확장됩니다)
	 *
	 * @param value 추가할 값(Null 불가)
	 * @return 항상 {@code true}
	 * @throws NullPointerException 값이 {@code null}인 경우
	 */
	@Override
	public boolean offerLast(T value) {
		addLast(value);
		return true;
	}

	/**
	 * 앞쪽 요소를 제거하고 반환합니다.
	 *
	 * @return 제거된 값
	 * @throws NoSuchElementException 비어 있는 경우
	 * @implNote Amortized O(1)
	 */
	@Override
	public T removeFirst() {
		T v = pollFirst();
		if (v == null) throw new NoSuchElementException();
		return v;
	}

	/**
	 * 앞쪽 요소를 제거하고 반환합니다. 비어 있으면 {@code null}을 반환합니다.
	 *
	 * @return 제거된 값 또는 {@code null}
	 * @implNote Amortized O(1)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T pollFirst() {
		if (size == 0) return null;
		T v = (T) elements[head];
		elements[head] = null;
		head = inc(head);
		size--; modCount++;
		return v;
	}

	/**
	 * 뒤쪽 요소를 제거하고 반환합니다.
	 *
	 * @return 제거된 값
	 * @throws NoSuchElementException 비어 있는 경우
	 * @implNote Amortized O(1)
	 */
	@Override
	public T removeLast() {
		T v = pollLast();
		if (v == null) throw new NoSuchElementException();
		return v;
	}

	/**
	 * 뒤쪽 요소를 제거하고 반환합니다. 비어 있으면 {@code null}을 반환합니다.
	 *
	 * @return 제거된 값 또는 {@code null}
	 * @implNote Amortized O(1)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T pollLast() {
		if (size == 0) return null;
		tail = dec(tail);
		T v = (T) elements[tail];
		elements[tail] = null;
		size--; modCount++;
		return v;
	}

	/**
	 * 앞쪽 요소(가장 앞)를 반환합니다.
	 *
	 * @return 앞쪽 값
	 * @throws NoSuchElementException 비어 있는 경우
	 */
	@Override
	public T getFirst() {
		T v = peekFirst();
		if (v == null) throw new NoSuchElementException();
		return v;
	}

	/**
	 * 앞쪽 요소(가장 앞)를 반환합니다. 비어 있으면 {@code null}.
	 *
	 * @return 앞쪽 값 또는 {@code null}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T peekFirst() {
		return size == 0 ? null : (T) elements[head];
	}

	/**
	 * 뒤쪽 요소(가장 뒤)를 반환합니다.
	 *
	 * @return 뒤쪽 값
	 * @throws NoSuchElementException 비어 있는 경우
	 */
	@Override
	public T getLast() {
		T v = peekLast();
		if (v == null) throw new NoSuchElementException();
		return v;
	}

	/**
	 * 뒤쪽 요소(가장 뒤)를 반환합니다. 비어 있으면 {@code null}.
	 *
	 * @return 뒤쪽 값 또는 {@code null}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T peekLast() {
		return size == 0 ? null : (T) elements[dec(tail)];
	}

	/**
	 * 스택 API의 {@code push}로, {@link #addFirst(Object)}와 동일합니다.
	 *
	 * @param value 추가할 값(Null 불가)
	 * @throws NullPointerException 값이 {@code null}인 경우
	 */
	@Override
	public void push(T value) { addFirst(value); }

	/**
	 * 스택 API의 {@code pop}으로, {@link #removeFirst()}와 동일합니다.
	 *
	 * @return 제거된 값
	 * @throws NoSuchElementException 비어 있는 경우
	 */
	@Override
	public T pop() { return removeFirst(); }

	/**
	 * 스택 API의 {@code peek}으로, {@link #peekFirst()}와 동일합니다.
	 *
	 * @return 앞쪽 값 또는 {@code null}
	 */
	@Override
	public T peek() { return peekFirst(); }

	/**
	 * 주어진 값이 포함되어 있는지 확인합니다.
	 * (순차 탐색, Null 비교는 {@code equals} 기반)
	 *
	 * @param value 찾을 값(Null이면 항상 {@code false})
	 * @return 포함되어 있으면 {@code true}
	 */
	@Override
	public boolean contains(T value) {
		if (value == null) return false;
		for (int i = 0, idx = head; i < size; i++, idx = inc(idx)) {
			if (value.equals(elements[idx])) return true;
		}
		return false;
	}

	/**
	 * 모든 요소를 제거하고 초기 상태로 되돌립니다.
	 * 내부 배열의 참조를 {@code null}로 지워 GC가 가능하도록 합니다.
	 */
	@Override
	public void clear() {
		for (int i = 0, idx = head; i < size; i++, idx = inc(idx)) {
			elements[idx] = null;
		}
		head = tail = size = 0;
		modCount++;
	}

	/**
	 * 앞→뒤 순서로 순회하는 <b>fail-fast</b> 이터레이터를 반환합니다.
	 * 이터레이터 생성 이후 Deque의 구조가 변경되면
	 * {@link #hasNext()} 또는 {@link #next()} 호출 시
	 * {@link ConcurrentModificationException}이 발생합니다.
	 *
	 * @return 이터레이터
	 */
	@Override
	public Iterator<T> iterator() {
		return new Itr();
	}

	// ============ 내부 유틸 ============

	/**
	 * 원형 인덱스 증가 연산: {@code (i + 1) % elements.length}
	 */
	private int inc(int i) {
		return (i + 1) % elements.length;
	}

	/**
	 * 원형 인덱스 감소 연산: {@code (i - 1 + elements.length) % elements.length}
	 */
	private int dec(int i) {
		return (i - 1 + elements.length) % elements.length;
	}

	/**
	 * 요소 1개 추가에 충분한 용량을 보장합니다.
	 * 가득 찬 경우 내부 배열을 2배로 확장합니다.
	 */
	private void ensureCapacityForOneMore() {
		if (size == elements.length) {
			grow();
		}
	}

	/**
	 * 내부 배열을 2배로 확장하고, {@code head}부터 {@code size}개를
	 * 인덱스 0부터 연속 복사하여 순서를 보존합니다.
	 * 이후 {@code head=0}, {@code tail=size}로 재배치합니다.
	 */
	private void grow() {
		int newCap = Math.max(1, elements.length << 1);
		Object[] newArr = new Object[newCap];
		// head부터 size만큼 순서대로 복사
		for (int i = 0, idx = head; i < size; i++, idx = inc(idx)) {
			newArr[i] = elements[idx];
		}
		elements = newArr;
		head = 0;
		tail = size;
		// modCount는 상단 add에서 증가하므로 여기선 변경 X
	}

	// ============ Iterator (앞→뒤, fail-fast) ============

	/**
	 * 앞→뒤 순서로 순회하는 이터레이터 구현체입니다.
	 * 생성 시점의 {@code modCount}를 캡처하여 구조 변경을 감지합니다.
	 */
	private final class Itr implements Iterator<T> {
		private int cursor = head;
		private int seen = 0;
		private final int expectedMod = modCount;

		/**
		 * 다음 요소가 존재하는지 반환합니다.
		 * 구조 변경이 감지되면 {@link ConcurrentModificationException}.
		 *
		 * @return 다음 요소가 있으면 {@code true}
		 * @throws ConcurrentModificationException 구조 변경 시
		 */
		@Override
		public boolean hasNext() {
			checkForComod();
			return seen < size;
		}

		/**
		 * 다음 요소를 반환합니다.
		 * 더 이상 요소가 없으면 {@link NoSuchElementException}.
		 * 구조 변경이 감지되면 {@link ConcurrentModificationException}.
		 *
		 * @return 다음 값
		 * @throws NoSuchElementException 더 이상 요소가 없는 경우
		 * @throws ConcurrentModificationException 구조 변경 시
		 */
		@Override
		@SuppressWarnings("unchecked")
		public T next() {
			checkForComod();
			if (seen >= size) throw new NoSuchElementException();
			T v = (T) elements[cursor];
			cursor = inc(cursor);
			seen++;
			return v;
		}

		/**
		 * 생성 시점 이후의 구조 변경을 감지합니다.
		 *
		 * @throws ConcurrentModificationException 구조 변경 시
		 */
		private void checkForComod() {
			if (expectedMod != modCount) throw new ConcurrentModificationException();
		}
	}
}
