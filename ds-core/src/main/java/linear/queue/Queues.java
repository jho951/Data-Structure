package linear.queue;

import linear.queue.internal.QueueEx;

public final class Queues {
	private Queues() {}

	/** 원형 배열 기반 Queue */
	public static <T> MyQueue<T> array() {
		return new QueueEx<>();
	}

	/** 초기 용량 지정 */
	public static <T> MyQueue<T> array(int initialCapacity) {
		return new QueueEx<>(initialCapacity);
	}
}
