package linear.examples;

import linear.list.Lists;
import linear.list.MyList;

public class SinglyLinkedListExample {
    public static void main(String[] args) {
		MyList<Integer> testList1 = Lists.singlyLinked();
		testList1.add(1);
		for(int i : testList1) {
			System.out.println(i);
		}
	}
}
