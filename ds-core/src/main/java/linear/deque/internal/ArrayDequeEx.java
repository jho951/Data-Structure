package linear.deque.internal;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import linear.deque.MyDeque;

/**
 * 배열 기반 Deque (원형 버퍼 + 자동 확장)
 * 특성:
 * - null 불가 (NPE)
 * - 앞/뒤 O(1) (분할상환)
 * - 확장 시 2배
 * - fail-fast iterator
 */
public final class ArrayDequeEx<T> implements MyDeque<T> {

	private Object[] elements;
	private int head;   // 다음 removeFirst/peekFirst 위치
	private int tail;   // 다음 addLast/offerLast 위치
	private int size;
	private int modCount;

	private static final int DEFAULT_CAP = 8;

	public ArrayDequeEx() {
		this(DEFAULT_CAP);
	}

	public ArrayDequeEx(int initialCapacity) {
		if (initialCapacity < 1) initialCapacity = DEFAULT_CAP;
		// 2의 거듭제곱으로 맞추지는 않지만 원형 인덱싱은 모듈로 처리
		elements = new Object[initialCapacity];
	}

	// ============ MyDeque API ============
	@Override public int size() { return size; }
	@Override public boolean isEmpty() { return size == 0; }

	@Override
	public void addFirst(T value) {
		if (value == null) throw new NullPointerException("null not allowed");
		ensureCapacityForOneMore();
		head = dec(head);
		elements[head] = value;
		size++; modCount++;
	}

	@Override
	public boolean offerFirst(T value) {
		addFirst(value); // 무제한 확장 → 항상 true
		return true;
	}

	@Override
	public void addLast(T value) {
		if (value == null) throw new NullPointerException("null not allowed");
		ensureCapacityForOneMore();
		elements[tail] = value;
		tail = inc(tail);
		size++; modCount++;
	}

	@Override
	public boolean offerLast(T value) {
		addLast(value);
		return true;
	}

	@Override
	public T removeFirst() {
		T v = pollFirst();
		if (v == null) throw new NoSuchElementException();
		return v;
	}

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

	@Override
	public T removeLast() {
		T v = pollLast();
		if (v == null) throw new NoSuchElementException();
		return v;
	}

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

	@Override
	public T getFirst() {
		T v = peekFirst();
		if (v == null) throw new NoSuchElementException();
		return v;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T peekFirst() {
		return size == 0 ? null : (T) elements[head];
	}

	@Override
	public T getLast() {
		T v = peekLast();
		if (v == null) throw new NoSuchElementException();
		return v;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T peekLast() {
		return size == 0 ? null : (T) elements[dec(tail)];
	}

	@Override
	public void push(T value) { addFirst(value); }

	@Override
	public T pop() { return removeFirst(); }

	@Override
	public T peek() { return peekFirst(); }

	@Override
	public boolean contains(T value) {
		if (value == null) return false;
		for (int i = 0, idx = head; i < size; i++, idx = inc(idx)) {
			if (value.equals(elements[idx])) return true;
		}
		return false;
	}

	@Override
	public void clear() {
		for (int i = 0, idx = head; i < size; i++, idx = inc(idx)) {
			elements[idx] = null;
		}
		head = tail = size = 0;
		modCount++;
	}

	@Override
	public Iterator<T> iterator() {
		return new Itr();
	}

	// ============ 내부 유틸 ============

	private int inc(int i) {
		return (i + 1) % elements.length;
	}

	private int dec(int i) {
		return (i - 1 + elements.length) % elements.length;
	}

	private void ensureCapacityForOneMore() {
		if (size == elements.length) {
			grow();
		}
	}

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
	private final class Itr implements Iterator<T> {
		private int cursor = head;
		private int seen = 0;
		private final int expectedMod = modCount;

		@Override
		public boolean hasNext() {
			checkForComod();
			return seen < size;
		}

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

		private void checkForComod() {
			if (expectedMod != modCount) throw new ConcurrentModificationException();
		}
	}
}
