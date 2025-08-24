package linear.stack;

import linear.stack.internal.StackEx;

public final class Stacks {
	private Stacks() {} // 유틸 클래스

	/** Vector(2x) 확장 정책의 스택 생성 */
	public static <T> MyStack<T> vector() {
		return new StackEx<>();    // ← StackEx<T> 가 MyStack<T>를 구현하므로 OK
	}
}