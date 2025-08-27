package linear.examples;

import linear.list.Lists;
import linear.list.MyList;

public class DoublyLinkedListExample {
	public static void main(String[] args) {
		MyList<Integer> testList = Lists.doublyLinked();
		testList.add(1);
		for(int i : testList) {
			System.out.println(i);
		}
	}
}