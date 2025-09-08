package linear.deque;

import linear.deque.internal.ArrayDequeEx;
import linear.deque.internal.LinkedDequeEx;

public final class Deques {
	private Deques() {}

	public static <T> MyDeque<T> array() { return new ArrayDequeEx<>();}
	public static <T> MyDeque<T> arrayBlocking(int initialCapacity) {
		return new ArrayDequeEx<>(initialCapacity);
	}
	public static <T> MyDeque<T> linked() {return new LinkedDequeEx<>();}

}
