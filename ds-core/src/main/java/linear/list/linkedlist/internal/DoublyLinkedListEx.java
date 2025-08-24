package linear.list.linkedlist.internal;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import linear.list.MyList;

/**
 * 양방향 연결 리스트 (Doubly Linked List)
 * - add/remove 양끝 O(1)
 * - node 접근 O(n/2) (앞/뒤 선택 탐색)
 * - fail-fast iterator
 */
public final class DoublyLinkedListEx<T> implements MyList<T> {

	private static final class Node<T> {
		T item;
		Node<T> prev, next;
		Node(Node<T> prev, T item, Node<T> next) {
			this.item = item; this.prev = prev; this.next = next;
		}
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
		if (index == 0)   { linkFirst(value); return; }
		linkBefore(value, node(index));
	}

	@Override
	public T get(int index) { // O(n/2)
		range(index);
		return node(index).item;
	}

	@Override
	public T set(int index, T value) { // O(n/2)
		range(index);
		Node<T> n = node(index);
		T old = n.item;
		n.item = value;
		return old;
	}

	@Override
	public T remove(int index) { // O(n/2)
		range(index);
		if (index == 0)       return unlinkFirst();
		if (index == size-1)  return unlinkLast();
		return unlink(node(index));
	}

	@Override
	public void clear() { // O(n)
		Node<T> x = head;
		while (x != null) {
			Node<T> next = x.next;
			x.item = null; x.prev = null; x.next = null;
			x = next;
		}
		head = tail = null;
		size = 0;
		modCount++;
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
			// remove() 미구현 (필요 시 직접 추가 가능)
		};
	}

	/* ===== 내부 유틸 ===== */

	private void linkFirst(T value) {
		Node<T> oldHead = head;
		Node<T> n = new Node<>(null, value, oldHead);
		head = n;
		if (oldHead == null) tail = n;
		else oldHead.prev = n;
		size++; modCount++;
	}

	private void linkLast(T value) {
		Node<T> oldTail = tail;
		Node<T> n = new Node<>(oldTail, value, null);
		tail = n;
		if (oldTail == null) head = n;
		else oldTail.next = n;
		size++; modCount++;
	}

	private void linkBefore(T value, Node<T> succ) {
		Node<T> pred = succ.prev;
		Node<T> n = new Node<>(pred, value, succ);
		succ.prev = n;
		if (pred == null) head = n;
		else pred.next = n;
		size++; modCount++;
	}

	private T unlinkFirst() {
		Node<T> first = head;
		Node<T> next = first.next;
		T val = first.item;

		head = next;
		if (next == null) tail = null;
		else next.prev = null;

		first.item = null; first.next = null; first.prev = null;
		size--; modCount++;
		return val;
	}

	private T unlinkLast() {
		Node<T> last = tail;
		Node<T> prev = last.prev;
		T val = last.item;

		tail = prev;
		if (prev == null) head = null;
		else prev.next = null;

		last.item = null; last.next = null; last.prev = null;
		size--; modCount++;
		return val;
	}

	private T unlink(Node<T> n) {
		T val = n.item;
		Node<T> next = n.next;
		Node<T> prev = n.prev;

		if (prev == null) head = next; else prev.next = next;
		if (next == null) tail = prev; else next.prev = prev;

		n.item = null; n.prev = null; n.next = null;
		size--; modCount++;
		return val;
	}

	private Node<T> node(int index) {
		if (index < (size >> 1)) {
			Node<T> x = head;
			for (int i = 0; i < index; i++) x = x.next;
			return x;
		} else {
			Node<T> x = tail;
			for (int i = size - 1; i > index; i--) x = x.prev;
			return x;
		}
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
