package linear.examples;

import linear.list.MyList;
import linear.list.arraylist.internal.ArrayListEx;
import linear.list.linkedlist.internal.SinglyLinkedListEx;
import linear.stack.MyStack;
import linear.stack.internal.StackEx;

public class Demo {
    public static void main(String[] args) {
		MyList<Integer> a = new ArrayListEx<>();
		a.add(1);
		MyList<Integer> b = new SinglyLinkedListEx<>();
		b.add(1);
		MyStack<Integer> c = new StackEx<>();
		c.push(1);
    }
}
