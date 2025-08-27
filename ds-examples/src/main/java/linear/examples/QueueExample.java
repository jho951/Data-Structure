package linear.examples;

import linear.queue.MyQueue;
import linear.queue.Queues;

public class QueueExample {
	public static void main(String[] args) {
		MyQueue<Integer> testQueue1 = Queues.array();
		MyQueue<Integer> testQueue2 = Queues.linked();
		testQueue1.enqueue(1);
		testQueue2.enqueue(2);
		System.out.println(testQueue1);
		System.out.println(testQueue2);
	}
}
