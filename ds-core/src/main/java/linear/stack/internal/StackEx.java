package linear.stack.internal;

import java.util.ConcurrentModificationException;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import linear.stack.MyStack;

/**
 * <h1>Vector 기반 스택 (LIFO)</h1>
 * <p>
 * 내부 저장소로 <b>배열(Object[])</b>을 사용하고, 용량이 꽉 차면 <b>2배(Vector 정책)</b>로 확장하는
 * 고정 길이-확장형 스택 구현체입니다. {@code push}/{@code pop}/{@code peek} 모두
 * 스택 상단(top, 내부 배열의 끝)을 기준으로 O(1) 연산을 제공합니다.
 * </p>
 *
 * <h2>시간 복잡도</h2>
 * <table border="1" cellpadding="4" cellspacing="0">
 *   <thead>
 *     <tr><th>연산</th><th>시간 복잡도</th><th>비고</th></tr>
 *   </thead>
 *   <tbody>
 *     <tr><td>{@link #push(Object)}</td><td>암ortized O(1)</td><td>드물게 확장 시 O(n) (배열 재할당 및 복사)</td></tr>
 *     <tr><td>{@link #pop()}</td><td>O(1)</td><td>상단 원소 제거</td></tr>
 *     <tr><td>{@link #peek()}</td><td>O(1)</td><td>상단 원소 조회(제거 없음)</td></tr>
 *     <tr><td>{@link #size()}</td><td>O(1)</td><td>-</td></tr>
 *     <tr><td>{@link #isEmpty()}</td><td>O(1)</td><td>-</td></tr>
 *     <tr><td>{@link #clear()}</td><td>O(n)</td><td>참조 해제(null 대입)로 GC 도움</td></tr>
 *     <tr><td>{@link #iterator()}</td><td>O(1) 생성, O(1)/원소당 진행</td><td>bottom→top 순회, fail-fast</td></tr>
 *   </tbody>
 * </table>
 *
 * <h2>공간 복잡도</h2>
 * <ul>
 *   <li>저장 원소가 n개일 때 <b>O(n)</b> 공간 사용.</li>
 *   <li>용량(capacity)은 2배씩 증가하는 기하급수 성장으로, 평균적인 여유 공간(slack)은
 *       상수배 이내로 제한(최대 절반 미만)됩니다.</li>
 * </ul>
 *
 * <h2>설계 노트</h2>
 * <ul>
 *   <li><b>확장 정책</b>: 기존 용량의 2배(Overflow 가드 포함). 드문 확장 시에만 O(n) 비용 발생.</li>
 *   <li><b>fail-fast iterator</b>: 생성 시점의 {@code modCount} 스냅샷을 보관하여,
 *       순회 중 외부 구조 변경이 감지되면 {@link ConcurrentModificationException}을 던집니다.</li>
 *   <li><b>스레드-세이프 아님</b>: 외부 동기화 필요. (학습 목적 구현)</li>
 *   <li><b>null 허용</b>: {@code push(null)} 가능. (필요 시 정책 변경)</li>
 * </ul>
 *
 * <h2>예시</h2>
 * <pre>{@code
 * StackEx<Integer> s = new StackEx<>();
 * s.push(10); s.push(20); s.push(30);
 * int top = s.peek(); // 30
 * int popped = s.pop(); // 30
 * }</pre>
 *
 * @param <T> 원소 타입
 * @see java.util.Stack
 * @see java.util.ArrayDeque
 * @since 1.0
 */
public class StackEx<T> implements MyStack<T> {   // ← ★ 핵심: MyStack<T> 구현

	private static final int DEFAULT_CAPACITY = 10;
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	private Object[] elementData;
	private int size;
	private int modCount;

	public StackEx() {
		this.elementData = new Object[DEFAULT_CAPACITY];
	}

	public StackEx(int initialCapacity) {
		if (initialCapacity < 0) throw new IllegalArgumentException("capacity < 0");
		this.elementData = new Object[Math.max(initialCapacity, DEFAULT_CAPACITY)];
	}

	@Override public int size() { return size; }
	@Override public boolean isEmpty() { return size == 0; }

	@Override
	public void push(T value) {
		ensureCapacity(size + 1);
		elementData[size++] = value;
		modCount++;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T pop() {
		if (isEmpty()) throw new EmptyStackException();
		T val = (T) elementData[--size];
		elementData[size] = null;
		modCount++;
		return val;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T peek() {
		if (isEmpty()) throw new EmptyStackException();
		return (T) elementData[size - 1];
	}

	@Override
	public void clear() {
		for (int i = 0; i < size; i++) elementData[i] = null;
		size = 0;
		modCount++;
	}

	private void ensureCapacity(int minCap) {
		if (minCap <= elementData.length) return;
		int old = elementData.length;
		long doubled = (long) old * 2L;
		int newCap = (int) Math.min(doubled, (long) MAX_ARRAY_SIZE);
		if (newCap < minCap) newCap = Math.min(minCap, MAX_ARRAY_SIZE);
		if (newCap < minCap) throw new OutOfMemoryError("Required array size too large");

		Object[] newArr = new Object[newCap];
		System.arraycopy(elementData, 0, newArr, 0, size);
		elementData = newArr;
	}

	// MyStack extends Iterable<T> 이므로 iterator 구현 필요
	@Override
	public Iterator<T> iterator() {
		final int expected = modCount;
		return new Iterator<T>() {
			int cursor = 0;
			@Override public boolean hasNext() { return cursor < size; }
			@SuppressWarnings("unchecked")
			@Override public T next() {
				if (expected != modCount) throw new ConcurrentModificationException();
				if (cursor >= size) throw new NoSuchElementException();
				return (T) elementData[cursor++];
			}
		};
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		for (int i=0; i<size; i++) {
			sb.append(elementData[i]);
			if (i+1 < size) sb.append(", ");
		}
		return sb.append("]").toString();
	}
}