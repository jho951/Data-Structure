package nonLinear.tree.general.internal;

import nonLinear.tree.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * 일반 트리의 내부 노드 구현체입니다.
 */
public final class GeneralNode<T> implements Position<T> {
	public T element;
	public GeneralNode<T> parent;
	public final List<GeneralNode<T>> children = new ArrayList<>();

	public GeneralNode(T element, GeneralNode<T> parent) {
		this.element = element;
		this.parent = parent;
	}

	@Override
	public T element() {
		return element;
	}
}
