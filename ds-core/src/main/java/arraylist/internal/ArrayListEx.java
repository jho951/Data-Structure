package ArrayList.internal;

import ArrayList.MyList;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Simple dynamic array implementation (not thread-safe).
 * - Growth policy: 1.5x
 * - Fail-fast iterator (modCount)
 */
public final class ArrayListEx<T> implements MyList<T> {
	private static final int DEFAULT_CAPACITY = 10;

	private Object[] elements;
	private int size;
	private int modCount; // for fail-fast iterator

	public ArrayListEx(int initialCapacity) {
		if (initialCapacity < 0) throw new IllegalArgumentException("capacity < 0");
		elements = new Object[Math.max(DEFAULT_CAPACITY, initialCapacity)];
	}

	public ArrayListEx() {
		this(DEFAULT_CAPACITY);
	}

	@Override
	public int size() { return size; }

	@Override
	public boolean isEmpty() { return size == 0; }

	@Override
	public void add(T value) {
		ensureCapacity(size + 1);
		elements[size++] = value;
		modCount++;
	}

	@Override
	public void add(int index, T value) {
		rangeForAdd(index);
		ensureCapacity(size + 1);
		System.arraycopy(elements, index, elements, index + 1, size - index);
		elements[index] = value;
		size++;
		modCount++;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(int index) {
		range(index);
		return (T) elements[index];
	}

	@SuppressWarnings("unchecked")
	@Override
	public T set(int index, T value) {
		range(index);
		T old = (T) elements[index];
		elements[index] = value;
		return old;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T remove(int index) {
		range(index);
		T old = (T) elements[index];
		int move = size - index - 1;
		if (move > 0) {
			System.arraycopy(elements, index + 1, elements, index, move);
		}
		elements[--size] = null; // help GC
		modCount++;
		return old;
	}

	@Override
	public void clear() {
		Arrays.fill(elements, 0, size, null);
		size = 0;
		modCount++;
	}

	@Override
	public Iterator<T> iterator() {
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

	/* ---------- helpers ---------- */

	private void ensureCapacity(int min) {
		if (min <= elements.length) return;
		int newCap = Math.max(elements.length + (elements.length >> 1), min); // 1.5x
		elements = Arrays.copyOf(elements, newCap);
	}

	private void range(int i) {
		if (i < 0 || i >= size) {
			throw new IndexOutOfBoundsException("index=" + i + ", size=" + size);
		}
	}

	private void rangeForAdd(int i) {
		if (i < 0 || i > size) {
			throw new IndexOutOfBoundsException("index=" + i + ", size=" + size);
		}
	}
}
