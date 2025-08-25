package linear.list.linkedlist.internal;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import linear.list.MyList;

/**
 * 단방향 연결 리스트 (Singly Linked List)
 * - tail 보유로 add(value) O(1)
 * - get/set/remove(index) O(n)
 * - fail-fast iterator
 */
public final class SinglyLinkedListEx<T> implements MyList<T> {

	private static final class Node<T> {
		T item;
		Node<T> next;
		Node(T item) { this.item = item; }
	}

	private Node<T> head;
	private Node<T> tail;
	private int size;
	private int modCount;

	@Override public int size() { return size; }
	@Override public boolean isEmpty() { return size == 0; }

	@Override
	public void add(T value) { // O(1)
		linkLast(value);
	}

	@Override
	public void add(int index, T value) { // O(n)
		rangeForAdd(index);
		if (index == size) { linkLast(value); return; }
		if (index == 0) {
			Node<T> n = new Node<>(value);
			n.next = head;
			head = n;
			if (tail == null) tail = n;
			size++; modCount++;
			return;
		}
		Node<T> prev = node(index - 1);
		Node<T> n = new Node<>(value);
		n.next = prev.next;
		prev.next = n;
		size++; modCount++;
	}

	@Override
	public T get(int index) { // O(n)
		range(index);
		return node(index).item;
	}

	@Override
	public T set(int index, T value) { // O(n)
		range(index);
		Node<T> n = node(index);
		T old = n.item;
		n.item = value;
		return old;
	}

	@Override
	public T remove(int index) { // O(n)
		range(index);
		if (index == 0) return unlinkFirst();
		Node<T> prev = node(index - 1);
		return unlinkNext(prev);
	}

	@Override
	public void clear() { // O(n)
		Node<T> x = head;
		while (x != null) {
			Node<T> next = x.next;
			x.item = null; x.next = null;
			x = next;
		}
		head = tail = null;
		size = 0; modCount++;
	}

	@Override
	public Iterator<T> iterator() {
		final int expected = modCount; // fail-fast
		return new Iterator<T>() {
			Node<T> cursor = head;

			@Override public boolean hasNext() { return cursor != null; }

			@Override public T next() {
				if (expected != modCount) throw new ConcurrentModificationException();
				if (cursor == null) throw new NoSuchElementException();
				T val = cursor.item;
				cursor = cursor.next;
				return val;
			}
		};
	}

	/* ===== 내부 유틸 ===== */

	private void linkLast(T value) {
		Node<T> n = new Node<>(value);
		if (tail == null) {
			head = tail = n;
		} else {
			tail.next = n;
			tail = n;
		}
		size++; modCount++;
	}

	private T unlinkFirst() {
		Node<T> first = head;
		T val = first.item;
		head = first.next;
		if (head == null) tail = null;
		first.item = null; first.next = null;
		size--; modCount++;
		return val;
	}

	private T unlinkNext(Node<T> prev) {
		Node<T> target = prev.next;
		T val = target.item;
		prev.next = target.next;
		if (target == tail) tail = prev;
		target.item = null; target.next = null;
		size--; modCount++;
		return val;
	}

	private Node<T> node(int index) {
		Node<T> x = head;
		for (int i = 0; i < index; i++) x = x.next;
		return x;
	}

	private void range(int i) {
		if (i < 0 || i >= size) throw new IndexOutOfBoundsException("index=" + i + ", size=" + size);
	}
	private void rangeForAdd(int i) {
		if (i < 0 || i > size) throw new IndexOutOfBoundsException("index=" + i + ", size=" + size);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		for (Node<T> n = head; n != null; n = n.next) {
			sb.append(n.item);
			if (n.next != null) sb.append(", ");
		}
		return sb.append("]").toString();
	}
}
