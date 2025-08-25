package linear.queue.internal;

import linear.queue.MyQueue;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

/**
 * <h1>원형 배열 기반 큐 (FIFO)</h1>
 * <p>내부 저장소로 배열(Object[])을 사용하고, 가득 차면 용량을 2배로 확장합니다.
 * head에서 꺼내고(take), tail에 넣는(put) 원형 버퍼 구조입니다.</p>
 *
 * <h2>시간 복잡도</h2>
 * enqueue: amortized O(1) / dequeue: O(1) / peek: O(1) / clear: O(n)
 *
 * <h2>공간 복잡도</h2>
 * 원소 n개 저장 시 O(n). 확장 시 새 배열 O(n) 필요.
 *
 * <h2>기타</h2>
 * - fail-fast iterator (modCount 스냅샷 비교)
 * - null 허용 (정책상 허용)
 * - 스레드-세이프 아님
 */
public final class ArrayQueueEx<T> implements MyQueue<T> {

	private static final int DEFAULT_CAPACITY = 8;
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	private Object[] elements;
	private int head;      // 다음에 꺼낼 위치
	private int tail;      // 다음에 넣을 위치
	private int size;
	private int modCount;  // 구조 변경 횟수 (fail-fast)

	public ArrayQueueEx() {
		this.elements = new Object[DEFAULT_CAPACITY];
	}

	public ArrayQueueEx(int initialCapacity) {
		if (initialCapacity < 0) throw new IllegalArgumentException("capacity < 0");
		int cap = Math.max(initialCapacity, DEFAULT_CAPACITY);
		this.elements = new Object[cap];
	}

	@Override public int size() { return size; }
	@Override public boolean isEmpty() { return size == 0; }

	@Override
	public void enqueue(T value) {
		ensureCapacity(size + 1);
		elements[tail] = value;
		tail = (tail + 1) % elements.length;
		size++;
		modCount++;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T dequeue() {
		if (isEmpty()) throw new NoSuchElementException("queue is empty");
		T val = (T) elements[head];
		elements[head] = null; // GC 도움
		head = (head + 1) % elements.length;
		size--;
		modCount++;
		return val;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T peek() {
		if (isEmpty()) throw new NoSuchElementException("queue is empty");
		return (T) elements[head];
	}

	@Override
	public void clear() {
		// 선형 슬롯만 null 처리 (원형이지만 size만큼만 돌면 충분)
		for (int i = 0, idx = head; i < size; i++, idx = (idx + 1) % elements.length) {
			elements[idx] = null;
		}
		head = tail = 0;
		size = 0;
		modCount++;
	}

	/** 필요 시 2배 확장 */
	private void ensureCapacity(int minCap) {
		if (minCap <= elements.length) return;
		int old = elements.length;
		int newCap = (int) Math.min((long) old * 2L, (long) MAX_ARRAY_SIZE);
		if (newCap < minCap) newCap = Math.min(minCap, MAX_ARRAY_SIZE);
		if (newCap < minCap) throw new OutOfMemoryError("Required array size too large");

		Object[] newArr = new Object[newCap];
		// head부터 size개를 0..size-1로 복사
		for (int i = 0, idx = head; i < size; i++, idx = (idx + 1) % old) {
			newArr[i] = elements[idx];
		}
		elements = newArr;
		head = 0;
		tail = size;
	}

	@Override
	public Iterator<T> iterator() {
		final int expected = modCount;
		return new Iterator<T>() {
			int i = 0;
			int idx = head;

			@Override public boolean hasNext() { return i < size; }

			@SuppressWarnings("unchecked")
			@Override public T next() {
				if (expected != modCount) throw new ConcurrentModificationException();
				if (i >= size) throw new NoSuchElementException();
				T v = (T) elements[idx];
				idx = (idx + 1) % elements.length;
				i++;
				return v;
			}
		};
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0, idx = head; i < size; i++, idx = (idx + 1) % elements.length) {
			sb.append(elements[idx]);
			if (i + 1 < size) sb.append(", ");
		}
		return sb.append("]").toString();
	}
}
