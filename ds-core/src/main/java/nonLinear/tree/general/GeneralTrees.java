package nonLinear.tree.general;

import nonLinear.tree.*;
import nonLinear.tree.general.internal.GeneralNode;
import java.util.*;

/**
 * N-ary 일반 트리 구현체 (자식 수 제한 없음)
 */
public class GeneralTrees<T> implements MyGeneralTree<T> {

	private GeneralNode<T> root;
	private int size;
	private int modCount; // fail-fast iterator 용

	@Override
	public int size() { return size; }

	@Override
	public boolean isEmpty() { return size == 0; }

	@Override
	public boolean contains(T value) {
		for (T v : this) {
			if (Objects.equals(v, value)) return true;
		}
		return false;
	}

	@Override
	public void clear() {
		root = null;
		size = 0;
		modCount++;
	}

	@Override
	public Position<T> root() { return root; }

	@Override
	public Position<T> addRoot(T value) {
		if (root != null) throw new IllegalStateException("루트가 이미 존재합니다.");
		root = new GeneralNode<>(value, null);
		size = 1;
		modCount++;
		return root;
	}

	@Override
	public Position<T> addChild(Position<T> parent, T value) {
		GeneralNode<T> p = cast(parent);
		GeneralNode<T> child = new GeneralNode<>(value, p);
		p.children.add(child);
		size++;
		modCount++;
		return child;
	}

	@Override
	public Position<T> parent(Position<T> p) {
		return cast(p).parent;
	}

	@Override
	public Iterable<Position<T>> children(Position<T> p) {
		return Collections.unmodifiableList(cast(p).children);
	}

	@Override
	public int removeSubtree(Position<T> p) {
		GeneralNode<T> node = cast(p);
		if (node.parent == null) { // 루트 삭제
			int removed = size;
			clear();
			return removed;
		}
		node.parent.children.remove(node);
		int removed = countNodes(node);
		size -= removed;
		modCount++;
		return removed;
	}

	private int countNodes(GeneralNode<T> n) {
		int cnt = 0;
		Queue<GeneralNode<T>> q = new ArrayDeque<>();
		q.offer(n);
		while (!q.isEmpty()) {
			GeneralNode<T> cur = q.poll();
			cnt++;
			q.addAll(cur.children);
		}
		return cnt;
	}

	private GeneralNode<T> cast(Position<T> p) {
		if (!(p instanceof GeneralNode))
			throw new IllegalArgumentException("잘못된 Position");
		return (GeneralNode<T>) p;
	}

	// 기본 순회 = Preorder
	@Override
	public Iterator<T> iterator() {
		return new PreorderIterator();
	}

	private class PreorderIterator implements Iterator<T> {
		private final int expected = modCount;
		private final Deque<GeneralNode<T>> stack = new ArrayDeque<>();

		PreorderIterator() { if (root != null) stack.push(root); }

		private void check() {
			if (expected != modCount) throw new ConcurrentModificationException();
		}

		@Override
		public boolean hasNext() { return !stack.isEmpty(); }

		@Override
		public T next() {
			check();
			if (stack.isEmpty()) throw new NoSuchElementException();
			GeneralNode<T> n = stack.pop();
			for (int i = n.children.size() - 1; i >= 0; i--) {
				stack.push(n.children.get(i));
			}
			return n.element;
		}
	}
}
