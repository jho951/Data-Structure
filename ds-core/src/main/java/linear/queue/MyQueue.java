package linear.queue;

public interface MyQueue<T> extends Iterable<T> {
	int size();
	boolean isEmpty();

	/** 뒤에 삽입 (enqueue) */
	void enqueue(T value);

	/** 앞에서 제거+반환 (dequeue). 비어있으면 예외 */
	T dequeue();

	/** 앞(헤드) 조회(제거X). 비어있으면 예외 */
	T peek();

	/** 전체 비우기 */
	void clear();
}
