package nonLinear;

import nonLinear.tree.Position;
import nonLinear.tree.general.GeneralTrees;
import nonLinear.tree.general.MyGeneralTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GeneralTrees (N-ary) 단위 테스트
 * - JUnit 5 기준
 * - 실패 시 메시지를 통해 어떤 조건이 깨졌는지 명확히 표기
 */
class GeneralTreesTest {

	/** 전위순회 결과를 리스트로 수집하는 헬퍼 */
	private static <T> List<T> preorderValues(MyGeneralTree<T> tree) {
		List<T> out = new ArrayList<>();
		for (T v : tree) out.add(v);
		return out;
	}

	@Nested
	@DisplayName("기본 상태 & 루트 생성")
	class RootBasics {
		@Test
		@DisplayName("빈 트리 초기 상태: root==null, size==0, isEmpty==true")
		void emptyTreeState() {
			MyGeneralTree<String> tree = new GeneralTrees<>();
			assertNull(tree.root(), "빈 트리는 root()가 null이어야 합니다");
			assertEquals(0, tree.size(), "빈 트리의 size는 0이어야 합니다");
			assertTrue(tree.isEmpty(), "빈 트리는 isEmpty==true 여야 합니다");
		}

		@Test
		@DisplayName("addRoot 후 상태 확인")
		void addRoot() {
			MyGeneralTree<String> tree = new GeneralTrees<>();
			Position<String> r = tree.addRoot("A");

			assertNotNull(r, "addRoot는 유효한 Position을 반환해야 합니다");
			assertEquals("A", r.element(), "루트의 element는 'A' 여야 합니다");
			assertEquals("A", tree.root().element(), "root()의 element도 'A' 여야 합니다");
			assertEquals(1, tree.size(), "루트 추가 후 size는 1이어야 합니다");
			assertFalse(tree.isEmpty(), "루트 추가 후 isEmpty==false 여야 합니다");
		}

		@Test
		@DisplayName("이미 루트가 있으면 addRoot는 예외")
		void addRootTwice() {
			MyGeneralTree<String> tree = new GeneralTrees<>();
			tree.addRoot("A");
			assertThrows(IllegalStateException.class, () -> tree.addRoot("B"),
				"이미 루트가 있을 때 addRoot는 IllegalStateException 이어야 합니다");
		}
	}

	@Nested
	@DisplayName("자식 추가 & 부모/자식 조회")
	class ChildrenAndParent {
		@Test
		@DisplayName("addChild로 자식 추가 & parent/children 검증")
		void addChildAndParent() {
			MyGeneralTree<String> tree = new GeneralTrees<>();
			Position<String> r = tree.addRoot("A");
			Position<String> b = tree.addChild(r, "B");
			Position<String> c = tree.addChild(r, "C");
			Position<String> d = tree.addChild(b, "D");

			assertEquals("A", r.element());
			assertEquals("B", b.element());
			assertEquals("C", c.element());
			assertEquals("D", d.element());

			// parent()
			assertNull(tree.parent(r), "루트의 parent는 null이어야 합니다");
			assertEquals(r.element(), tree.parent(b).element());
			assertEquals(r.element(), tree.parent(c).element());
			assertEquals(b.element(), tree.parent(d).element());

			// children() 순서 (삽입 순서 보존)
			List<String> rootChildren = new ArrayList<>();
			for (Position<String> p : tree.children(r)) rootChildren.add(p.element());
			assertEquals(List.of("B", "C"), rootChildren, "children는 삽입 순서를 보존해야 합니다");
		}

		@Test
		@DisplayName("children()은 읽기 전용 뷰: iterator.remove() 금지")
		void childrenReadOnly() {
			MyGeneralTree<String> tree = new GeneralTrees<>();
			Position<String> r = tree.addRoot("A");
			tree.addChild(r, "B");
			tree.addChild(r, "C");

			Iterator<Position<String>> it = tree.children(r).iterator();
			assertTrue(it.hasNext());
			it.next();
			assertThrows(UnsupportedOperationException.class, it::remove,
				"children()의 iterator.remove()는 UnsupportedOperationException 이어야 합니다");
		}

		@Test
		@DisplayName("다른 트리의 Position을 넘기면 예외")
		void positionFromAnotherTree() {
			MyGeneralTree<String> t1 = new GeneralTrees<>();
			MyGeneralTree<String> t2 = new GeneralTrees<>();
			Position<String> r1 = t1.addRoot("A");
			Position<String> r2 = t2.addRoot("X");

			assertThrows(IllegalArgumentException.class, () -> t1.addChild(r2, "BAD"),
				"서로 다른 트리의 Position 사용은 IllegalArgumentException 이어야 합니다");
		}
	}

	@Nested
	@DisplayName("순회 & contains")
	class TraversalAndContains {
		@Test
		@DisplayName("기본 이터레이터는 Preorder: A B D C")
		void preorderIterator() {
			MyGeneralTree<String> tree = new GeneralTrees<>();
			Position<String> r = tree.addRoot("A");
			Position<String> b = tree.addChild(r, "B");
			tree.addChild(r, "C");
			tree.addChild(b, "D");

			List<String> pre = preorderValues(tree);
			assertEquals(List.of("A", "B", "D", "C"), pre, "Preorder 순회 결과가 예상과 일치해야 합니다");
		}

		@Test
		@DisplayName("contains: 값 동등성(Objects.equals) 기준, null 포함")
		void containsValues() {
			MyGeneralTree<String> tree = new GeneralTrees<>();
			Position<String> r = tree.addRoot(null);  // null 허용 정책
			Position<String> b = tree.addChild(r, "B");
			tree.addChild(b, "C");

			assertTrue(tree.contains(null), "null 값도 포함 검사에 true 여야 합니다");
			assertTrue(tree.contains("B"));
			assertTrue(tree.contains("C"));
			assertFalse(tree.contains("Z"));
		}
	}

	@Nested
	@DisplayName("서브트리 삭제 & clear")
	class RemoveSubtreeAndClear {
		@Test
		@DisplayName("removeSubtree(비루트): 삭제 수 반환 & 연결 해제 & size 감소")
		void removeNonRootSubtree() {
			MyGeneralTree<String> tree = new GeneralTrees<>();
			Position<String> r = tree.addRoot("A");
			Position<String> b = tree.addChild(r, "B");
			Position<String> c = tree.addChild(r, "C");
			Position<String> d = tree.addChild(b, "D");
			// 현재 노드: A,B,C,D (size=4), Preorder = A B D C

			int removed = tree.removeSubtree(b);
			assertEquals(2, removed, "B 서브트리(B,D) 삭제 수는 2여야 합니다");
			assertEquals(2, tree.size(), "삭제 후 size는 2여야 합니다");

			List<String> pre = preorderValues(tree);
			assertEquals(List.of("A", "C"), pre, "B 서브트리 삭제 후 Preorder는 A C 여야 합니다");
		}

		@Test
		@DisplayName("removeSubtree(루트): 전체 clear와 동일")
		void removeRootSubtree() {
			MyGeneralTree<String> tree = new GeneralTrees<>();
			Position<String> r = tree.addRoot("A");
			tree.addChild(r, "B");
			tree.addChild(r, "C");

			int removed = tree.removeSubtree(r);
			assertEquals(3, removed, "루트 삭제면 전체 노드 수(3)를 반환해야 합니다");
			assertEquals(0, tree.size());
			assertNull(tree.root());
			assertTrue(tree.isEmpty());
		}

		@Test
		@DisplayName("clear(): 전체 초기화 및 GC 대상화")
		void clearAll() {
			MyGeneralTree<String> tree = new GeneralTrees<>();
			Position<String> r = tree.addRoot("A");
			tree.addChild(r, "B");
			tree.addChild(r, "C");

			tree.clear();
			assertEquals(0, tree.size());
			assertTrue(tree.isEmpty());
			assertNull(tree.root());
		}
	}

	@Nested
	@DisplayName("Fail-Fast 이터레이터")
	class FailFastIterator {
		@Test
		@DisplayName("순회 도중 구조 변경 시 ConcurrentModificationException")
		void concurrentModification() {
			MyGeneralTree<String> tree = new GeneralTrees<>();
			Position<String> r = tree.addRoot("A");
			tree.addChild(r, "B");
			tree.addChild(r, "C");

			Iterator<String> it = tree.iterator();
			assertEquals("A", it.next()); // 한 번 소비

			// 구조 변경 (자식 추가)
			tree.addChild(r, "D");

			assertThrows(ConcurrentModificationException.class, it::next,
				"이터레이터 생성 후 구조 변경되면 ConcurrentModificationException 이어야 합니다");
		}
	}

	@Nested
	@DisplayName("문서화된 계약 준수")
	class Contracts {
		@Test
		@DisplayName("iterator: hasNext()/next() 경계 조건")
		void iteratorBounds() {
			MyGeneralTree<Integer> tree = new GeneralTrees<>();
			Position<Integer> r = tree.addRoot(1);

			Iterator<Integer> it = tree.iterator();
			assertTrue(it.hasNext());
			assertEquals(1, it.next());
			assertFalse(it.hasNext());
			assertThrows(NoSuchElementException.class, it::next,
				"더 이상 원소가 없을 때 next()는 NoSuchElementException 이어야 합니다");
		}
	}
}
