package linear.stack;

public interface MyStack<T> extends Iterable<T> {
	int size();
	boolean isEmpty();
	void push(T value);
	T pop();
	T peek();
	void clear();
}
