package linear.queue.internal;

import linear.queue.MyQueue;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 연결 리스트 기반 Queue (FIFO)
 * - head: dequeue 지점
 * - tail: enqueue 지점
 *
 * <h2>시간 복잡도</h2>
 * enqueue: O(1)
 * dequeue: O(1)
 * peek: O(1)
 * clear: O(n)
 *
 * <h2>특징</h2>
 * - 확장 필요 없음 (노드 동적 생성)
 * - null 값 허용
 * - fail-fast iterator 지원
 */
public final class LinkedQueueEx<T> implements MyQueue<T> {

	/** 내부 노드 구조 */
	private static final class Node<E> {
		E item;
		Node<E> next;
		Node(E item) { this.item = item; }
	}

	private Node<T> head;
	private Node<T> tail;
	private int size;
	private int modCount;

	@Override
	public int size() { return size; }

	@Override
	public boolean isEmpty() { return size == 0; }

	@Override
	public void enqueue(T value) {
		Node<T> n = new Node<>(value);
		if (tail == null) { // 비어있을 때
			head = tail = n;
		} else {
			tail.next = n;
			tail = n;
		}
		size++;
		modCount++;
	}

	@Override
	public T dequeue() {
		if (isEmpty()) throw new NoSuchElementException("queue is empty");
		Node<T> n = head;
		head = head.next;
		if (head == null) tail = null; // 마지막 요소 제거 시 tail도 null 처리
		size--;
		modCount++;
		return n.item;
	}

	@Override
	public T peek() {
		if (isEmpty()) throw new NoSuchElementException("queue is empty");
		return head.item;
	}

	@Override
	public void clear() {
		Node<T> cur = head;
		while (cur != null) {
			Node<T> next = cur.next;
			cur.item = null;
			cur.next = null;
			cur = next;
		}
		head = tail = null;
		size = 0;
		modCount++;
	}

	@Override
	public Iterator<T> iterator() {
		final int expected = modCount;
		return new Iterator<T>() {
			Node<T> cur = head;

			@Override
			public boolean hasNext() { return cur != null; }

			@Override
			public T next() {
				if (expected != modCount) throw new ConcurrentModificationException();
				if (cur == null) throw new NoSuchElementException();
				T v = cur.item;
				cur = cur.next;
				return v;
			}
		};
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
