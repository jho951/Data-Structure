package linear.list.linkedlist;

import list.MyList;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 단방향 링크드 리스트 구현체
 * - list.MyList<T> 계약 준수
 * - 시간복잡도(단방향 기준):
 *   add(value): O(1)   // tail 보유
 *   add(index): O(n)
 *   get/set(index): O(n)
 *   remove(index): O(n)
 *   clear(): O(n)      // GC 도움 위해 링크 끊기
 */
public class SinglyLinkedList<T> implements MyList<T> {

	/* ===== 노드 ===== */
	private static final class Node<E> {
		E item;
		Node<E> next;
		Node(E item) { this.item = item; }
	}

	/* ===== 상태 ===== */
	private Node<T> head;
	private Node<T> tail;
	private int size;
	private int modCount; // fail-fast iterator용

	/* ===== MyList 구현 ===== */
	@Override
	public int size() { return size; }

	@Override
	public boolean isEmpty() { return size == 0; }

	/** 끝에 추가: O(1) */
	@Override
	public void add(T value) {
		Node<T> n = new Node<>(value);
		if (head == null) {
			head = tail = n;
		} else {
			tail.next = n;
			tail = n;
		}
		size++; modCount++;
	}

	/** index에 삽입: O(n) */
	@Override
	public void add(int index, T value) {
		rangeCheckForAdd(index);
		if (index == size) { // 맨 뒤
			add(value);
			return;
		}
		Node<T> newNode = new Node<>(value);
		if (index == 0) {
			newNode.next = head;
			head = newNode;
			if (tail == null) tail = head;
		} else {
			Node<T> prev = nodeAt(index - 1);
			newNode.next = prev.next;
			prev.next = newNode;
		}
		size++; modCount++;
	}

	/** O(n) */
	@Override
	public T get(int index) {
		rangeCheck(index);
		return nodeAt(index).item;
	}

	/** O(n), 이전 값 반환 */
	@Override
	public T set(int index, T value) {
		rangeCheck(index);
		Node<T> n = nodeAt(index);
		T old = n.item;
		n.item = value;
		return old;
	}

	/** O(n) */
	@Override
	public T remove(int index) {
		rangeCheck(index);
		Node<T> removed;
		if (index == 0) {
			removed = head;
			head = head.next;
			if (head == null) tail = null;
		} else {
			Node<T> prev = nodeAt(index - 1);
			removed = prev.next;
			prev.next = removed.next;
			if (removed == tail) tail = prev;
		}
		size--; modCount++;
		return removed.item;
	}

	/** O(n) */
	@Override
	public void clear() {
		// GC 도움: 링크 끊기
		Node<T> cur = head;
		while (cur != null) {
			Node<T> next = cur.next;
			cur.item = null;
			cur.next = null;
			cur = next;
		}
		head = tail = null;
		size = 0; modCount++;
	}

	/* ===== Iterator (fail-fast) ===== */
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private Node<T> next = head;
			private final int expectedMod = modCount;

			private void checkComod() {
				if (expectedMod != modCount)
					throw new ConcurrentModificationException();
			}

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public T next() {
				checkComod();
				if (next == null) throw new NoSuchElementException();
				T val = next.item;
				next = next.next;
				return val;
			}
		};
	}

	/* ===== 헬퍼 ===== */
	private Node<T> nodeAt(int index) {
		Node<T> n = head;
		for (int i = 0; i < index; i++) n = n.next;
		return n;
	}

	private void rangeCheck(int index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
	}

	private void rangeCheckForAdd(int index) {
		if (index < 0 || index > size)
			throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		Node<T> n = head;
		while (n != null) {
			sb.append(n.item);
			n = n.next;
			if (n != null) sb.append(", ");
		}
		return sb.append("]").toString();
	}
}
