package linear.examples;

import linear.list.Lists;
import linear.list.MyList;

public class LinkedListExample {
	public static void main(String[] args) {
		MyList<Integer> testList = Lists.singlyLinked();
		testList.add(1);
		System.out.println(testList);
		testList.add(2);
		System.out.println(testList);
	}
}
