package nonLinear.tree.general;

import nonLinear.tree.MyTree;
import nonLinear.tree.Position;

public interface MyGeneralTree<T> extends MyTree<T> {

	/** 루트 노드 반환 (없으면 null) */
	Position<T> root();

	/** 루트 노드 생성 (이미 루트가 있으면 예외) */
	Position<T> addRoot(T value);

	/** 부모 노드 자식으로 새로운 노드 추가 */
	Position<T> addChild(Position<T> parent, T value);

	/** 부모 반환 (루트면 null) */
	Position<T> parent(Position<T> p);

	/** 자식 노드 목록 반환 */
	Iterable<Position<T>> children(Position<T> p);

	/** 해당 위치를 루트로 하는 서브트리 삭제 */
	int removeSubtree(Position<T> p);
}
