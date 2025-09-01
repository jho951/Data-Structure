package nonLinear.tree.binary;

import nonLinear.tree.MyTree;
import nonLinear.tree.Position;

public interface MyBinaryTree<T> extends MyTree<T> {

	/** 루트 반환(없으면 null) */
	Position<T> root();

	/** 루트 생성(이미 있으면 예외) */
	Position<T> addRoot(T value);

	/** 왼쪽 자식 추가(이미 있으면 예외) */
	Position<T> addLeft(Position<T> parent, T value);

	/** 오른쪽 자식 추가(이미 있으면 예외) */
	Position<T> addRight(Position<T> parent, T value);

	/** 부모/왼쪽/오른쪽 접근(없으면 null) */
	Position<T> parent(Position<T> p);
	Position<T> left(Position<T> p);
	Position<T> right(Position<T> p);

	/** 해당 위치의 값을 새 값으로 교체하고, 기존 값을 반환 */
	T set(Position<T> p, T newValue);

	/** 해당 위치를 루트로 하는 서브트리 삭제, 삭제된 노드 수 반환 */
	int removeSubtree(Position<T> p);

	/** 유틸 순회기(선택): 전위/중위/후위 Iterable */
	Iterable<T> preorderIterable();
	Iterable<T> inorderIterable();   // 이진 트리 표준 중위
	Iterable<T> postorderIterable();
}
