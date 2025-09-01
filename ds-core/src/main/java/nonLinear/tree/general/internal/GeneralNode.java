package nonLinear.tree.general.internal;

import nonLinear.tree.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * 일반 트리의 내부 노드 구현체입니다.
 */
public final class GeneralNode<T> implements Position<T> {
	/** 이 노드가 담고 있는 값 */
	public T element;
	/** 부모 노드 */
	public GeneralNode<T> parent;
	/** 자식들 목록 */
	public final List<GeneralNode<T>> children = new ArrayList<>();

	/** 이 노드를 소유한 트리를 식별하는 토큰 (동일 트리인지 검증용) */
	public final Object ownerToken;

	public GeneralNode(T element, GeneralNode<T> parent, Object ownerToken) {
		this.element = element;
		this.parent = parent;
		this.ownerToken = ownerToken;
	}

	@Override
	public T element() {
		return element;
	}
}
