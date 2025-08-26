package linear.queue;

import linear.queue.internal.LinkedQueueEx;
import linear.queue.internal.ArrayQueueEx;

public final class Queues {
	private Queues() {}

	/** 원형 배열 기반 Queue */
	public static <T> MyQueue<T> array() {
		return new ArrayQueueEx<>();
	}

	/** 초기 용량 지정 */
	public static <T> MyQueue<T> array(int initialCapacity) {
		return new ArrayQueueEx<>(initialCapacity);
	}

	/** 연결 리스트 기반 Queue */
	public static <T> MyQueue<T> linked() {
		return new LinkedQueueEx<>();
	}
}
