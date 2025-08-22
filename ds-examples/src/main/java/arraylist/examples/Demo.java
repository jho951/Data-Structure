package arraylist.examples;

import arraylist.Lists;
import arraylist.MyList;


public class Demo {
    public static void main(String[] args) {
        MyList<Integer> list = Lists.arrayList();
        list.add(1);
        list.add(3);
        list.add(1, 2);
		list.set(1,3);
		list.get(0);
		list.remove(1);

        for (int v : list) {
            System.out.println(v);
        }
    }
}
