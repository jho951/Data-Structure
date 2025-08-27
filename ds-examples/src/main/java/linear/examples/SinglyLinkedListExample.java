package linear.examples;

import linear.list.Lists;
import linear.list.MyList;

public class SinglyLinkedListExample {
    public static void main(String[] args) {
		MyList<Integer> testList = Lists.singlyLinked();
		testList.add(1);
		for(int i : testList) {
			System.out.println(i);
		}
	}
}
