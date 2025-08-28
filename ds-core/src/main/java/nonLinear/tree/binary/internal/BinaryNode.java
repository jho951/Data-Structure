package nonLinear.tree.binary.internal;

import nonLinear.tree.Position;

public final class BinaryNode<T> implements Position<T> {
	public T element;
	public BinaryNode<T> parent;
	public BinaryNode<T> left;
	public BinaryNode<T> right;

	/** 이 노드를 소유한 트리를 식별하는 토큰 (동일 트리인지 검증용) */
	public final Object ownerToken;


	public BinaryNode(T element, BinaryNode<T> parent, Object ownerToken) {
		this.element = element;
		this.parent = parent;
		this.ownerToken = ownerToken;
	}

	@Override
	public T element() {
		return element;
	}
}
