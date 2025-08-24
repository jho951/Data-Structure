package list.linkedlist;

import list.MyList;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 양방향 링크드 리스트 (Doubly Linked List)
 *
 * 특징:
 * - head <-> ... <-> tail 양방향 연결
 * - 양끝 삽입/삭제 O(1)
 * - 임의 인덱스 접근 O(n) (앞/뒤 방향 최적 탐색)
 */
public class DoublyLinkedList<T> implements MyList<T> {

	/* ===== 내부 노드 클래스 ===== */
	private static final class Node<E> {
		E item;
		Node<E> prev;
		Node<E> next;
		Node(E item) { this.item = item; }
	}

	/* ===== 필드 ===== */
	private Node<T> head;
	private Node<T> tail;
	private int size;
	private int modCount; // fail-fast iterator 용

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
			n.prev = tail;
			tail = n;
		}
		size++; modCount++;
	}

	/** 인덱스 삽입: O(n) */
	@Override
	public void add(int index, T value) {
		rangeCheckForAdd(index);
		if (index == size) { // 맨 끝
			add(value);
			return;
		}
		Node<T> cur = nodeAt(index);
		Node<T> newNode = new Node<>(value);

		newNode.next = cur;
		newNode.prev = cur.prev;

		if (cur.prev != null) cur.prev.next = newNode;
		else head = newNode; // index == 0일 때

		cur.prev = newNode;
		size++; modCount++;
	}

	@Override
	public T get(int index) {
		rangeCheck(index);
		return nodeAt(index).item;
	}

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
		Node<T> target = nodeAt(index);
		unlink(target);
		return target.item;
	}

	@Override
	public void clear() {
		Node<T> cur = head;
		while (cur != null) {
			Node<T> next = cur.next;
			cur.item = null;
			cur.next = null;
			cur.prev = null;
			cur = next;
		}
		head = tail = null;
		size = 0; modCount++;
	}

	/* ===== Iterator (fail-fast, forward only) ===== */
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

	/* ===== 내부 유틸 ===== */

	/** 앞/뒤에서 가까운 쪽을 골라 탐색 → O(n/2) 최적화 */
	private Node<T> nodeAt(int index) {
		if (index < (size >> 1)) {
			Node<T> n = head;
			for (int i = 0; i < index; i++) n = n.next;
			return n;
		} else {
			Node<T> n = tail;
			for (int i = size - 1; i > index; i--) n = n.prev;
			return n;
		}
	}

	private void unlink(Node<T> target) {
		Node<T> prev = target.prev;
		Node<T> next = target.next;

		if (prev != null) prev.next = next;
		else head = next; // 맨 앞 제거

		if (next != null) next.prev = prev;
		else tail = prev; // 맨 뒤 제거

		size--; modCount++;
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

	/* ===== 테스트 예시 ===== */
	public static void main(String[] args) {
		MyList<String> list = new DoublyLinkedList<>();
		list.add("a");
		list.add("b");
		list.add("d");
		list.add(2, "c"); // [a, b, c, d]
		System.out.println(list);

		list.remove(1);   // remove "b"
		System.out.println(list); // [a, c, d]

		list.set(1, "X"); // [a, X, d]
		System.out.println(list);

		for (String s : list) {
			System.out.println("iter: " + s);
		}
	}
}
