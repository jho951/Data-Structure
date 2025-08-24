package linear.examples;

import linear.list.MyList;
import linear.list.arraylist.internal.ArrayListEx;
import linear.list.linkedlist.internal.SinglyLinkedListEx;

public class Demo {
    public static void main(String[] args) {
		MyList<Integer> a = new ArrayListEx<>();
		a.add(1);
		MyList<Integer> b = new SinglyLinkedListEx<>();
		b.add(2);

    }
}
