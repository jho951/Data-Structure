package linear.examples;

import linear.list.Lists;
import linear.list.MyList;
import linear.queue.MyQueue;
import linear.queue.Queues;
import linear.stack.MyStack;
import linear.stack.internal.StackEx;

public class Demo {
    public static void main(String[] args) {
		MyList<Integer> a = Lists.array();
		a.add(1);
		MyList<Integer> b = Lists.singly();
		b.add(1);
		MyList<Integer> c = Lists.doubly();
		c.add(1);
		MyStack<Integer> d = new StackEx<>();
		d.push(1);
		MyQueue<Integer> e = Queues.array();
    }
}
