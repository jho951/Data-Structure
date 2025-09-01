package nonLinear.tree.binary.internal;

import nonLinear.tree.Position;

/**
 * 이진 트리의 내부 노드 구현체입니다.
 * 외부에는 Position<T>라는 핸들(인터페이스) 로만 노출됩니다.
 * 실제 링크 구조(부모/자식)는 이 내부 클래스가 책임집니다.
 * final이므로 상속 불가 → 노드 구조가 임의로 바뀌지 않도록 고정.
 * @param <T>
 */
public final class BinaryNode<T> implements Position<T> {
	/** 이 노드가 담고 있는 값 */
	public T element;
	/** 부모노드 */
	public BinaryNode<T> parent;
	/** 왼쪽 자식 */
	public BinaryNode<T> left;
	/** 오른쪽 자식 */
	public BinaryNode<T> right;
	/** 이 노드를 소유한 트리를 식별하는 토큰 (동일 트리인지 검증용) 소유 트리 식별 토큰 */
	public final Object ownerToken;

	/**
	 * 노드 생성 시 값/부모/소유 트리를 초기 고정합니다.
	 * 왼쪽/오른쪽 자식은 기본 null, 이후 addLeft/addRight에서 연결.
	 * @param element 노드 값
	 * @param parent 부모노드
	 * @param ownerToken 트리 식별 토큰
	 */
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
