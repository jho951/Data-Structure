package ArrayList.examples;

import ArrayList.Lists;
import ArrayList.MyList;


public class Demo {
    public static void main(String[] args) {
        MyList<Integer> list = Lists.arrayList();
        list.add(1);
        list.add(2);
        list.add(1, 99);

        for (int v : list) {
            System.out.println(v);
        }
    }
}
