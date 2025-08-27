package nonLinear.tree;

/**
 * 모든 트리의 최상위 인터페이스입니다.
 * @param <T>
 */
public interface MyTree<T> extends Iterable<T> {
	int size();
	boolean isEmpty();
	boolean contains(T value);
	void clear();
}