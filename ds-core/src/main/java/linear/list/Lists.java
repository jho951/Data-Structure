package linear.list;

import linear.list.arraylist.internal.ArrayListEx;
import linear.list.linkedlist.internal.DoublyLinkedListEx;
import linear.list.linkedlist.internal.SinglyLinkedListEx;

public final class Lists {
	private Lists() {}

	public static <T> MyList<T> array() { return new ArrayListEx<>(); }
	public static <T> MyList<T> arrayBlocking(int initialCapacity)  { return new ArrayListEx<>(initialCapacity); }
	public static <T> MyList<T> singlyLinked() { return new SinglyLinkedListEx<>(); }
	public static <T> MyList<T> doublyLinked() { return new DoublyLinkedListEx<>(); }
}
