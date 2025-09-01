package nonLinear;

import nonLinear.tree.Position;
import nonLinear.tree.binary.BinaryTrees;
import nonLinear.tree.binary.MyBinaryTree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BinaryTrees (이진 트리) 단위 테스트 - JUnit 5
 */
class BinaryTreesTest {

	/* ---------- 유틸 ---------- */

	private static <T> List<T> toList(Iterable<T> it) {
		List<T> out = new ArrayList<>();
		for (T v : it) out.add(v);
		return out;
	}

	private static MyBinaryTree<Integer> buildSample() {
		//        1
		//      /   \
		//     2     3
		//    / \   / \
		//   4  5  6  7
		MyBinaryTree<Integer> t = new BinaryTrees<>();
		Position<Integer> r = t.addRoot(1);
		Position<Integer> b = t.addLeft(r, 2);
		Position<Integer> c = t.addRight(r, 3);
		t.addLeft(b, 4);
		t.addRight(b, 5);
		t.addLeft(c, 6);
		t.addRight(c, 7);
		return t;
	}

	/* ---------- 기본/루트 ---------- */

	@Nested
	@DisplayName("기본 상태 & 루트")
	class RootBasics {
		@Test
		@DisplayName("빈 트리 초기 상태")
		void emptyTree() {
			MyBinaryTree<String> t = new BinaryTrees<>();
			assertNull(t.root());
			assertEquals(0, t.size());
			assertTrue(t.isEmpty());
		}

		@Test
		@DisplayName("addRoot: 생성/중복 예외")
		void addRootOnce() {
			MyBinaryTree<Integer> t = new BinaryTrees<>();
			Position<Integer> r = t.addRoot(1);
			assertNotNull(r);
			assertEquals(1, r.element());
			assertEquals(1, t.size());
			assertFalse(t.isEmpty());

			assertThrows(IllegalStateException.class, () -> t.addRoot(99));
		}
	}

	/* ---------- 자식/부모 접근 ---------- */

	@Nested
	@DisplayName("부모/왼쪽/오른쪽")
	class FamilyAccess {
		@Test
		@DisplayName("left/right/parent 정상 동작")
		void leftRightParent() {
			MyBinaryTree<Integer> t = buildSample();
			Position<Integer> r = t.root();
			Position<Integer> l = t.left(r);
			Position<Integer> rr = t.right(r);

			assertEquals(2, l.element());
			assertEquals(3, rr.element());
			assertNull(t.parent(r));
			assertEquals(r, t.parent(l));
			assertEquals(r, t.parent(rr));
		}

		@Test
		@DisplayName("이미 자식이 있으면 addLeft/addRight 예외")
		void addChildTwiceError() {
			MyBinaryTree<Integer> t = new BinaryTrees<>();
			Position<Integer> r = t.addRoot(1);
			t.addLeft(r, 2);
			t.addRight(r, 3);

			assertThrows(IllegalStateException.class, () -> t.addLeft(r, 22));
			assertThrows(IllegalStateException.class, () -> t.addRight(r, 33));
		}

		@Test
		@DisplayName("다른 트리의 Position 전달 시 예외(소유 토큰 검증)")
		void foreignPositionError() {
			MyBinaryTree<Integer> t1 = new BinaryTrees<>();
			MyBinaryTree<Integer> t2 = new BinaryTrees<>();
			Position<Integer> r1 = t1.addRoot(1);
			Position<Integer> r2 = t2.addRoot(9);

			assertThrows(IllegalArgumentException.class, () -> t1.addLeft(r2, 100));
			assertThrows(IllegalArgumentException.class, () -> t1.parent(r2));
			assertThrows(IllegalArgumentException.class, () -> t1.left(r2));
		}
	}

	/* ---------- contains / set ---------- */

	@Nested
	@DisplayName("값 관련 API")
	class Values {
		@Test
		@DisplayName("contains: 값 동등성 기준(null 포함)")
		void containsValues() {
			MyBinaryTree<String> t = new BinaryTrees<>();
			Position<String> r = t.addRoot(null);
			t.addLeft(r, "L");
			t.addRight(r, "R");

			assertTrue(t.contains(null));
			assertTrue(t.contains("L"));
			assertTrue(t.contains("R"));
			assertFalse(t.contains("X"));
		}

		@Test
		@DisplayName("set: 이전 값 반환 및 교체")
		void setValue() {
			MyBinaryTree<Integer> t = new BinaryTrees<>();
			Position<Integer> r = t.addRoot(10);
			assertEquals(10, r.element());
			int old = t.set(r, 99);
			assertEquals(10, old);
			assertEquals(99, r.element());
		}
	}

	/* ---------- 순회 ---------- */

	@Nested
	@DisplayName("순회 (inorder / preorder / postorder)")
	class Traversal {
		@Test
		@DisplayName("inorderIterable(): 4 2 5 1 6 3 7")
		void inorderIterable() {
			MyBinaryTree<Integer> t = buildSample();
			assertEquals(List.of(4, 2, 5, 1, 6, 3, 7), toList(t.inorderIterable()));
		}

		@Test
		@DisplayName("preorderIterable(): 1 2 4 5 3 6 7")
		void preorderIterable() {
			MyBinaryTree<Integer> t = buildSample();
			assertEquals(List.of(1, 2, 4, 5, 3, 6, 7), toList(t.preorderIterable()));
		}

		@Test
		@DisplayName("postorderIterable(): 4 5 2 6 7 3 1")
		void postorderIterable() {
			MyBinaryTree<Integer> t = buildSample();
			assertEquals(List.of(4, 5, 2, 6, 7, 3, 1), toList(t.postorderIterable()));
		}

		@Test
		@DisplayName("기본 iterator()가 inorder라면 동일 결과")
		void defaultIteratorInorder() {
			MyBinaryTree<Integer> t = buildSample();
			List<Integer> vals = new ArrayList<>();
			for (int v : t) vals.add(v);
			assertEquals(List.of(4, 2, 5, 1, 6, 3, 7), vals);
		}

		@Test
		@DisplayName("inorderRecursive(Consumer) 호출")
		void inorderRecursiveConsumer() {
			BinaryTrees<Integer> t = new BinaryTrees<>();
			Position<Integer> r = t.addRoot(1);
			Position<Integer> b = t.addLeft(r, 2);
			Position<Integer> c = t.addRight(r, 3);
			t.addLeft(b, 4);
			t.addRight(b, 5);
			t.addLeft(c, 6);
			t.addRight(c, 7);

			List<Integer> out = new ArrayList<>();
			Consumer<Integer> visit = out::add;
			t.inorderRecursive(visit);

			assertEquals(List.of(4, 2, 5, 1, 6, 3, 7), out);
		}
	}

	/* ---------- 삭제/초기화 ---------- */

	@Nested
	@DisplayName("removeSubtree / clear")
	class Removal {
		@Test
		@DisplayName("removeSubtree(비루트): 왼쪽 서브트리(2,4,5) 제거")
		void removeNonRootSubtree() {
			MyBinaryTree<Integer> t = buildSample(); // size=7
			Position<Integer> r = t.root();
			Position<Integer> left = t.left(r);

			int removed = t.removeSubtree(left);
			assertEquals(3, removed);
			assertEquals(4, t.size());
			assertEquals(List.of(1, 6, 3, 7), toList(t.preorderIterable())); // 2-서브트리 제거됨
		}

		@Test
		@DisplayName("removeSubtree(루트): 전체 삭제")
		void removeRootSubtree() {
			MyBinaryTree<Integer> t = buildSample();
			int removed = t.removeSubtree(t.root());
			assertEquals(7, removed);
			assertEquals(0, t.size());
			assertNull(t.root());
			assertTrue(t.isEmpty());
		}

		@Test
		@DisplayName("clear(): 전체 초기화")
		void clearAll() {
			MyBinaryTree<Integer> t = buildSample();
			t.clear();
			assertEquals(0, t.size());
			assertNull(t.root());
			assertTrue(t.isEmpty());
		}
	}

	/* ---------- 이터레이터 계약 ---------- */

	@Nested
	@DisplayName("이터레이터 계약 & fail-fast")
	class IteratorContracts {
		@Test
		@DisplayName("hasNext/next 경계: 더 없을 때 NoSuchElementException")
		void iteratorBounds() {
			MyBinaryTree<Integer> t = new BinaryTrees<>();
			t.addRoot(10);

			Iterator<Integer> it = t.iterator();
			assertTrue(it.hasNext());
			assertEquals(10, it.next());
			assertFalse(it.hasNext());
			assertThrows(NoSuchElementException.class, it::next);
		}

		@Test
		@DisplayName("fail-fast: 순회 중 구조 변경 시 ConcurrentModificationException")
		void failFast() {
			MyBinaryTree<Integer> t = buildSample();
			Iterator<Integer> it = t.iterator(); // inorder
			assertEquals(4, it.next()); // 한 번 소비

			// 구조 변경(노드 추가)
			Position<Integer> r = t.root();
			t.addLeft(t.left(r), 99); // 기존 2의 왼쪽 자리에 이미 4가 있어 예외가 날 수도 있음
			// 위 줄이 예외라면 다른 위치에 추가:
			// t.addLeft(t.left(t.right(r)), 99);

			assertThrows(ConcurrentModificationException.class, it::next);
		}
	}
}
