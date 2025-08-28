package nonLinear.tree;

/**
 * 트리 자료구조를 위한 최상위 인터페이스입니다.
 * 공통적으로 가져야 할 기능으로, 최소한 이 기능들을 제공해야 한다는 계약입니다.
 * 인터페이스 분리와 다형성을 반영한 구조입니다.
 * @param <T>
 */
public interface MyTree<T> extends Iterable<T> {
	/** 트리에 들어 있는 원소 개수 */
	int size();
	/** 비어 있는지 확인 */
	boolean isEmpty();
	/** 특정 원소 포함 여부 */
	boolean contains(T value);
	/** 트리 초기화 (모든 원소 삭제) */
	void clear();
}