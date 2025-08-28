package linear.examples;

import linear.stack.MyStack;
import linear.stack.Stacks;

public class StackExample {
	public static void main(String[] args) {
		MyStack<Integer>testStack = Stacks.vector();
		testStack.push(1);
		for(int i : testStack) {
			System.out.println(i);
		}
	}
}
