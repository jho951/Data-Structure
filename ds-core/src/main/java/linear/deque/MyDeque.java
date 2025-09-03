package linear.deque;

/**
 * 간단한 Deque 인터페이스 (양방향 큐)
 * - java.util.Deque API를 기반으로 작성
 * - 제네릭 & Iterable 지원
 */
public interface MyDeque<T> extends Iterable<T> {

	int size();
	boolean isEmpty();

	/**
	 * 앞쪽 삽입 (실패 시 예외)
	 * @param value 데이터
	 */
	void addFirst(T value);
	/**
	 * 앞쪽 삽입 (실패 시 false)
	 * @param value 데이터
	 */
	boolean offerFirst(T value);
	/**
	 * 뒤쪽 삽입 (실패 시 예외)
	 * @param value 데이터
	 */
	void addLast(T value);
	/**
	 * 뒤쪽 삽입 (실패 시 false)
	 * @param value 데이터
	 */
	boolean offerLast(T value);
	/** 앞쪽 삭제 후 반환 (없으면 예외) */
	T removeFirst();
	/** 앞쪽 삭제 후 반환 (없으면 null) */
	T pollFirst();
	/** 뒤쪽 삭제 후 반환 (없으면 예외) */
	T removeLast();
	/** 뒤쪽 삭제 후 반환 (없으면 null) */
	T pollLast();
	/** 앞쪽 요소 조회 (없으면 예외) */
	T getFirst();
	/** 앞쪽 요소 조회 (없으면 null) */
	T peekFirst();
	/** 뒤쪽 요소 조회 (없으면 예외) */
	T getLast();
	/**  뒤쪽 요소 조회 (없으면 null) */
	T peekLast();


	void push(T value);
	T pop();
	T peek();

	boolean contains(T value);
	void clear();
}
