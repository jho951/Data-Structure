package linear.deque.internal;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import linear.deque.MyDeque;

/**
 * 연결 리스트 기반 Deque (양방향 노드)
 * 특성:
 * - null 불가 (NPE)
 * - 앞/뒤 삽입/삭제 O(1)
 * - 순회/contains O(n)
 * - fail-fast iterator
 */
public final class LinkedDequeEx<T> implements MyDeque<T> {

	private static final class Node<E> {
		E item;
		Node<E> prev;
		Node<E> next;
		Node(E item) { this.item = item; }
	}

	private Node<T> head; // 첫 노드
	private Node<T> tail; // 마지막 노드
	private int size;
	private int modCount;

	// ============ MyDeque API ============
	@Override public int size() { return size; }
	@Override public boolean isEmpty() { return size == 0; }

	@Override
	public void addFirst(T value) {
		if (value == null) throw new NullPointerException("null not allowed");
		Node<T> n = new Node<>(value);
		Node<T> h = head;
		n.next = h;
		head = n;
		if (h == null) {
			tail = n;
		} else {
			h.prev = n;
		}
		size++; modCount++;
	}

	@Override
	public boolean offerFirst(T value) {
		addFirst(value);
		return true;
	}

	@Override
	public void addLast(T value) {
		if (value == null) throw new NullPointerException("null not allowed");
		Node<T> n = new Node<>(value);
		Node<T> t = tail;
		n.prev = t;
		tail = n;
		if (t == null) {
			head = n;
		} else {
			t.next = n;
		}
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
	public T pollFirst() {
		if (head == null) return null;
		Node<T> h = head;
		T v = h.item;
		Node<T> next = h.next;
		head = next;
		if (next == null) {
			tail = null;
		} else {
			next.prev = null;
		}
		// help GC
		h.item = null; h.next = null;
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
	public T pollLast() {
		if (tail == null) return null;
		Node<T> t = tail;
		T v = t.item;
		Node<T> prev = t.prev;
		tail = prev;
		if (prev == null) {
			head = null;
		} else {
			prev.next = null;
		}
		// help GC
		t.item = null; t.prev = null;
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
	public T peekFirst() {
		return head == null ? null : head.item;
	}

	@Override
	public T getLast() {
		T v = peekLast();
		if (v == null) throw new NoSuchElementException();
		return v;
	}

	@Override
	public T peekLast() {
		return tail == null ? null : tail.item;
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
		for (Node<T> n = head; n != null; n = n.next) {
			if (value.equals(n.item)) return true;
		}
		return false;
	}

	@Override
	public void clear() {
		// GC friendly unlink
		for (Node<T> n = head; n != null; ) {
			Node<T> next = n.next;
			n.item = null; n.prev = null; n.next = null;
			n = next;
		}
		head = tail = null;
		size = 0;
		modCount++;
	}

	@Override
	public Iterator<T> iterator() {
		return new Itr();
	}

	// ============ Iterator (앞→뒤, fail-fast) ============
	private final class Itr implements Iterator<T> {
		private Node<T> cursor = head;
		private final int expectedMod = modCount;

		@Override
		public boolean hasNext() {
			checkForComod();
			return cursor != null;
		}

		@Override
		public T next() {
			checkForComod();
			if (cursor == null) throw new NoSuchElementException();
			T v = cursor.item;
			cursor = cursor.next;
			return v;
		}

		private void checkForComod() {
			if (expectedMod != modCount) throw new ConcurrentModificationException();
		}
	}
}
