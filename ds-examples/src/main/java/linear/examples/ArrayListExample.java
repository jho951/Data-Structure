package linear.examples;

import linear.list.Lists;
import linear.list.MyList;

public class ArrayListExample {
	public static void main(String[] args) {
		MyList<Integer> testList = Lists.array();
		testList.add(2);
		testList.add(3);

		System.out.println(testList);

		testList.set(0,1);
		for(int i : testList) {
			System.out.print(i);
			System.out.print(", ");
		}
		System.out.println();

		testList.add(1,2);
		for(int i : testList) {
			System.out.print(i);
			System.out.print(", ");
		}
		System.out.println();

		testList.remove(2);
		for(int i : testList) {
			System.out.print(i);
			System.out.print(", ");
		}
		System.out.println();

		testList.clear();
		if(testList.isEmpty()) {
			System.out.print("비워졌습니다.");
		};

	}
}
