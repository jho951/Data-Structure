package list.examples;

import linear.list.Lists;
import list.MyList;


public class Demo {
    public static void main(String[] args) {
		MyList<String> a = Lists.singly();
		a.add("A"); a.add("B"); a.add(1, "X");
		System.out.println(a);

		MyList<String> b = Lists.doubly();
		b.add("1"); b.add("2"); b.add(0, "0");
		b.remove(1);
		System.out.println(b);

        MyList<Integer> c = Lists.array();
		c.add(1); c.add(2);
        c.add(1, 2);
		System.out.println(c);
    }
}
