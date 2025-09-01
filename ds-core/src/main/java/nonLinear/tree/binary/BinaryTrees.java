package nonLinear.tree.binary;

import nonLinear.tree.Position;
import nonLinear.tree.binary.internal.BinaryNode;

import java.util.*;
import java.util.function.Consumer;

/**
 * 이진 트리 구현 (fail-fast 이터레이터, 표준 중위 순회 제공)
 * - size/isEmpty: O(1)
 * - contains: O(n)
 * - addLeft/addRight/set: O(1)
 * - removeSubtree: O(k)
 * - 기본 iterator(): inorder
 */
public class BinaryTrees<T> implements MyBinaryTree<T> {

	private BinaryNode<T> root;
	private int size;
	private int modCount;

	// ===== MyTree 공통 =====
	@Override public int size() { return size; }
	@Override public boolean isEmpty() { return size == 0; }

	@Override
	public boolean contains(T value) {
		for (T v : this) {
			if (Objects.equals(v, value)) return true;
		}
		return false;
	}

	@Override
	public void clear() {
		// 전체 트리를 GC 대상화
		// 후위 순회로 참조를 끊어도 되지만, 루트만 끊어도 GC 가능
		root = null;
		size = 0;
		modCount++;
	}

	// ===== MyBinaryTree 전용 =====
	@Override public Position<T> root() { return root; }
	private final Object ownerToken = new Object();


	@Override
	public Position<T> addRoot(T value) {
		if (root != null) throw new IllegalStateException("루트가 이미 존재합니다.");
		root = new BinaryNode<>(value, null, ownerToken);
		size = 1;
		modCount++;
		return root;
	}

	@Override
	public Position<T> addLeft(Position<T> parent, T value) {
		BinaryNode<T> p = cast(parent);              // 여기서 owner 검증
		if (p.left != null) throw new IllegalStateException("왼쪽 자식이 이미 있습니다.");
		BinaryNode<T> n = new BinaryNode<>(value, p, ownerToken);
		p.left = n;
		size++; modCount++;
		return n;
	}

	@Override
	public Position<T> addRight(Position<T> parent, T value) {
		BinaryNode<T> p = cast(parent);              // 여기서 owner 검증
		if (p.right != null) throw new IllegalStateException("오른쪽 자식이 이미 있습니다.");
		BinaryNode<T> n = new BinaryNode<>(value, p, ownerToken);
		p.right = n;
		size++; modCount++;
		return n;
	}


	@Override public Position<T> parent(Position<T> p) { return cast(p).parent; }
	@Override public Position<T> left(Position<T> p) { return cast(p).left; }
	@Override public Position<T> right(Position<T> p) { return cast(p).right; }

	@Override
	public T set(Position<T> p, T newValue) {
		BinaryNode<T> n = cast(p);
		T old = n.element;
		n.element = newValue;
		// 값 교체는 구조 변경 아님 → modCount 증가 없음 (원한다면 정책에 따라 증가 가능)
		return old;
	}

	@Override
	public int removeSubtree(Position<T> p) {
		BinaryNode<T> n = cast(p);
		// 부모로부터 연결 해제
		if (n.parent == null) { // 루트 제거
			int removed = size;
			clear();            // modCount++ 포함
			return removed;
		} else {
			BinaryNode<T> parent = n.parent;
			if (parent.left == n) parent.left = null;
			else if (parent.right == n) parent.right = null;
		}

		int removed = countNodes(n);
		size -= removed;
		modCount++;
		return removed;
	}

	private int countNodes(BinaryNode<T> subRoot) {
		int cnt = 0;
		Deque<BinaryNode<T>> st = new ArrayDeque<>();
		st.push(subRoot);
		while (!st.isEmpty()) {
			BinaryNode<T> cur = st.pop();
			cnt++;
			if (cur.left != null) st.push(cur.left);
			if (cur.right != null) st.push(cur.right);
		}
		return cnt;
	}

	private BinaryNode<T> cast(Position<T> p) {
		if (!(p instanceof BinaryNode<T> node))
			throw new IllegalArgumentException("이 구현의 Position이 아닙니다.");
		if (node.ownerToken != this.ownerToken)
			throw new IllegalArgumentException("다른 트리에서 생성된 Position입니다.");
		return node;
	}

	// ===== 순회 (Iterable) =====
	/** 기본 이터레이터는 '중위(Inorder)' */
	@Override
	public Iterator<T> iterator() {
		return new InorderIterator();
	}

	@Override
	public Iterable<T> preorderIterable() {
		return PreorderIterator::new;
	}

	@Override
	public Iterable<T> inorderIterable() {
		return this;
	}

	@Override
	public Iterable<T> postorderIterable() {
		return PostorderIterator::new;
	}

	// ----- Inorder (반복 + 스택, fail-fast) -----
	private final class InorderIterator implements Iterator<T> {
		private final int expected = modCount;
		private final Deque<BinaryNode<T>> st = new ArrayDeque<>();
		private BinaryNode<T> cur = root;

		private void check() { if (expected != modCount) throw new ConcurrentModificationException(); }

		@Override public boolean hasNext() {
			return cur != null || !st.isEmpty();
		}

		@Override public T next() {
			check();
			while (cur != null) {
				st.push(cur);
				cur = cur.left;
			}
			if (st.isEmpty()) throw new NoSuchElementException();
			BinaryNode<T> n = st.pop();
			T val = n.element;
			cur = n.right;
			return val;
		}
	}

	// ----- Preorder (반복 + 스택, fail-fast) -----
	private final class PreorderIterator implements Iterator<T> {
		private final int expected = modCount;
		private final Deque<BinaryNode<T>> st = new ArrayDeque<>();

		PreorderIterator() { if (root != null) st.push(root); }
		private void check() { if (expected != modCount) throw new ConcurrentModificationException(); }

		@Override public boolean hasNext() { return !st.isEmpty(); }

		@Override public T next() {
			check();
			if (st.isEmpty()) throw new NoSuchElementException();
			BinaryNode<T> n = st.pop();
			if (n.right != null) st.push(n.right);
			if (n.left  != null) st.push(n.left);
			return n.element;
		}
	}

	// ----- Postorder (반복 + 스택 2개, fail-fast) -----
	private final class PostorderIterator implements Iterator<T> {
		private final int expected = modCount;
		private final Deque<BinaryNode<T>> s1 = new ArrayDeque<>();
		private final Deque<BinaryNode<T>> s2 = new ArrayDeque<>();

		PostorderIterator() {
			if (root != null) s1.push(root);
			while (!s1.isEmpty()) {
				BinaryNode<T> n = s1.pop();
				s2.push(n);
				if (n.left  != null) s1.push(n.left);
				if (n.right != null) s1.push(n.right);
			}
		}
		private void check() { if (expected != modCount) throw new ConcurrentModificationException(); }

		@Override public boolean hasNext() { return !s2.isEmpty(); }

		@Override public T next() {
			check();
			if (s2.isEmpty()) throw new NoSuchElementException();
			return s2.pop().element;
		}
	}

	// ===== (선택) 재귀 방문 유틸 =====
	public void inorderRecursive(Consumer<T> visit) { inorderRecursive(root, visit); }
	private void inorderRecursive(BinaryNode<T> n, Consumer<T> v) {
		if (n == null) return;
		inorderRecursive(n.left, v);
		v.accept(n.element);
		inorderRecursive(n.right, v);
	}

	public void preorderRecursive(Consumer<T> visit) { preorderRecursive(root, visit); }
	private void preorderRecursive(BinaryNode<T> n, Consumer<T> v) {
		if (n == null) return;
		v.accept(n.element);
		preorderRecursive(n.left, v);
		preorderRecursive(n.right, v);
	}

	public void postorderRecursive(Consumer<T> visit) { postorderRecursive(root, visit); }
	private void postorderRecursive(BinaryNode<T> n, Consumer<T> v) {
		if (n == null) return;
		postorderRecursive(n.left, v);
		postorderRecursive(n.right, v);
		v.accept(n.element);
	}
}
