package nonLinear.tree.general;

import nonLinear.tree.Position;
import nonLinear.tree.general.internal.GeneralNode;

import java.util.*;

/**
 * <h1>N-ary 일반 트리 구현체 (자식 수 제한 없음)</h1>
 *
 * <h2>설계 개요</h2>
 * - <b>Position</b> 핸들을 통해 외부에 "위치"만 노출하고, 실제 노드 구조(부모/자식 링크)는 숨깁니다.
 * - <b>일반 트리</b>이므로 자식 수에 제한이 없고, 정렬 규칙도 없습니다.
 * - <b>기본 이터레이터는 Preorder</b>(루트 → 자식들 순서)로 동작합니다.
 * - <b>fail-fast</b>를 위해 구조 변경 시 modCount를 증가시키고, 이터레이터는 생성 시점의 expected 값과 비교합니다.
 *
 * <h2>정책</h2>
 * - <b>null 요소</b>: 현재 구현은 허용(검증 없음). 금지하려면 addRoot/addChild에서 Objects.requireNonNull() 추가.
 * - <b>중복 값</b>: 허용(contains는 값 동등성 비교).
 * - <b>children()</b>: 외부 수정 방지 위해 <i>읽기 전용 뷰</i>를 반환합니다.
 * - <b>스레드 안전성</b>: 없음(단일 스레드 가정).
 *
 * <h2>불변식</h2>
 * - size는 트리의 전체 노드 수와 항상 일치해야 합니다.
 * - 각 GeneralNode의 parent/children 링크는 서로 일관성을 가져야 합니다.
 * - 구조 변경(clear, addRoot, addChild, removeSubtree)이 일어날 때마다 modCount가 증가해야 합니다.
 *
 * <h2>복잡도</h2>
 * - size(), isEmpty(): O(1)
 * - contains(x): O(n) (정렬 규칙 없음 → 전 노드 순회)
 * - addChild(p, v): O(1) (부모 p를 이미 알고 있을 때)
 * - removeSubtree(p): O(k) (p를 루트로 하는 서브트리 노드 수 k에 비례)
 * - iterator(): 전체 순회 O(n), 공간 O(h)~O(n) (순회 방식에 따라)
 */
public class GeneralTrees<T> implements MyGeneralTree<T> {

	// ====== MyTree 공통 ======
	/** @return 전체 노드 수 (O(1)) */
	@Override
	public int size() { return size; }

	/** @return 비어있는지 여부 (O(1)) */
	@Override
	public boolean isEmpty() { return size == 0; }

	/**
	 * 값 포함 여부 검사 (O(n))
	 * - 일반 트리는 정렬 규칙이 없으므로 선형 순회로 확인합니다.
	 * - equals 비교(Objects.equals) 사용 → null 안전
	 */
	@Override
	public boolean contains(T value) {
		for (T v : this) {
			if (Objects.equals(v, value)) return true;
		}
		return false;
	}
	/**
	 * 전체 비우기 (O(1))
	 * - 루트 참조를 끊고 size를 0으로 초기화
	 * - 내부 노드들은 GC 대상이 됩니다.
	 * - fail-fast를 위해 modCount 증가
	 */
	@Override
	public void clear() {
		root = null;
		size = 0;
		modCount++;
	}

	/** 트리의 루트(없으면 null) */
	private GeneralNode<T> root;

	/** 전체 노드 수 */
	private int size;

	/**
	 * 구조 변경 횟수 (fail-fast 용)
	 * - addRoot/addChild/clear/removeSubtree 등 "구조가 바뀌는" 연산 시 증가
	 * - 이터레이터는 생성 시점의 expected 값과 비교하여 변경 감지
	 */
	private int modCount;



	// ====== MyGeneralTree 전용 ======

	/** @return 루트 위치(없으면 null) */
	@Override
	public Position<T> root() { return root; }

	/** 이 트리를 식별하는 전용 토큰. 노드 생성 시 주입하여 소유 관계를 검증한다. */
	private final Object ownerToken = new Object();

	/**
	 * 루트 생성 (O(1))
	 * - 이미 루트가 있으면 IllegalStateException
	 * - 현재 구현은 value의 null을 허용합니다(정책에 따라 requireNonNull로 금지 가능)
	 */
	@Override
	public Position<T> addRoot(T value) {
		if (root != null) throw new IllegalStateException("루트가 이미 존재합니다.");
		root = new GeneralNode<>(value, null, ownerToken);
		size = 1;
		modCount++;
		return root;
	}

	/**
	 * 부모 p의 자식으로 노드 추가 (O(1))
	 * - p는 같은 트리에서 얻은 Position이어야 합니다.
	 * - 현재 구현은 value의 null을 허용합니다.
	 * - 중복 값 허용(contains 검사 없음)
	 */

	@Override
	public Position<T> addChild(Position<T> parent, T value) {
		GeneralNode<T> p = cast(parent); // 여기서 owner 검증 수행
		GeneralNode<T> child = new GeneralNode<>(value, p, ownerToken);
		p.children.add(child);
		size++;
		modCount++;
		return child;
	}

	/** @return p의 부모(루트면 null) (O(1)) */
	@Override
	public Position<T> parent(Position<T> p) {
		return cast(p).parent;
	}


	/**
	 * p의 자식들 (읽기 전용 뷰) 반환 (O(1))
	 * - 외부에서 children 목록을 변경할 수 없게 하기 위해 unmodifiableList 사용
	 * - 순서는 삽입 순서를 보장(ArrayList)
	 */
	@Override
	public Iterable<Position<T>> children(Position<T> p) {
		return java.util.Collections.unmodifiableList(cast(p).children);
	}

	/**
	 * 위치 p를 루트로 하는 <b>서브트리 전체</b> 삭제 (O(k))
	 * - k: p를 포함한 서브트리의 노드 수
	 * - 루트 삭제인 경우: clear() 호출로 전체 초기화
	 * - 비루트: 부모의 children에서 p를 제거한 뒤, 서브트리 크기를 BFS로 계산하여 size 감소
	 * - 삭제된 Position은 더 이상 <b>유효하지 않음</b>(재사용 금지)
	 *
	 * @return 삭제된 노드 수(k)
	 */
	@Override
	public int removeSubtree(Position<T> p) {
		GeneralNode<T> node = cast(p);
		if (node.parent == null) { // 루트 삭제(전체)
			int removed = size;
			clear();               // modCount++ 포함
			return removed;
		}
		// 부모-자식 링크를 먼저 끊어 외부 경로에서 접근할 수 없게 함
		node.parent.children.remove(node);

		// 서브트리 노드 수 계산: 큐(BFS)로 node 이하를 모두 세기
		int removed = countNodes(node);

		size -= removed;
		modCount++;
		return removed;
	}

	/**
	 * 서브트리 노드 수 세기 (BFS) (O(k))
	 * - 전달받은 n을 루트로 하는 영역만 순회합니다.
	 */
	private int countNodes(GeneralNode<T> n) {
		int cnt = 0;
		Queue<GeneralNode<T>> q = new ArrayDeque<>();
		q.offer(n);
		while (!q.isEmpty()) {
			GeneralNode<T> cur = q.poll();
			cnt++;
			q.addAll(cur.children);    // 현재 노드의 모든 자식을 큐에 추가
		}
		return cnt;
	}

	/**
	 * 외부에서 전달된 Position을 내부 노드로 검증/변환
	 * - 현재는 단순히 instanceof로만 확인합니다.
	 * - <b>개선 여지</b>: 서로 다른 트리의 Position 혼용 방지 위해 ownerId/removed 플래그 등을 GeneralNode에 두고 검증 가능
	 */
	@SuppressWarnings("unchecked")
	private GeneralNode<T> cast(Position<T> p) {
		if (!(p instanceof GeneralNode))
			throw new IllegalArgumentException("잘못된 Position: 이 구현의 노드가 아닙니다.");

		GeneralNode<T> node = (GeneralNode<T>) p;
		// 🔒 핵심: 다른 트리의 Position 혼용 방지
		if (node.ownerToken != this.ownerToken)
			throw new IllegalArgumentException("잘못된 Position: 다른 트리에서 생성된 Position입니다.");

		return node;
	}

	// ====== 순회(Iterable) ======

	/**
	 * 기본 순회 방식 = <b>Preorder</b> (루트 → 자식들)
	 * - children의 <b>삽입 순서</b>를 보존하여 방문합니다.
	 * - 전체 시간 O(n), 보조 공간은 높이/너비에 따라 O(h)~O(n)
	 */
	@Override
	public Iterator<T> iterator() {
		return new PreorderIterator();
	}

	/**
	 * Preorder 이터레이터 (fail-fast)
	 * - 스택을 사용해 루트부터 내려가며 방문
	 * - 자식들을 <b>역순으로 push</b>하여 pop 시 좌→우 순서가 유지되도록 함
	 * - 생성 이후 구조(modCount) 변경 시 next()에서 ConcurrentModificationException 발생
	 */
	private class PreorderIterator implements Iterator<T> {
		private final int expected = modCount;                 // 생성 시점의 구조 버전
		private final Deque<GeneralNode<T>> stack = new ArrayDeque<>();

		PreorderIterator() { if (root != null) stack.push(root); }

		/** 구조 변경 감지 (fail-fast) */
		private void check() {
			if (expected != modCount) throw new ConcurrentModificationException();
		}

		@Override
		public boolean hasNext() { return !stack.isEmpty(); }

		@Override
		public T next() {
			check();                                            // next() 호출 시점에만 검증(일반 패턴)
			if (stack.isEmpty()) throw new NoSuchElementException();

			GeneralNode<T> n = stack.pop();

			// children을 역순으로 push → pop 순서는 원래 삽입 순서가 되도록
			for (int i = n.children.size() - 1; i >= 0; i--) {
				stack.push(n.children.get(i));
			}
			return n.element;
		}

	}
}
