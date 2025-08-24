package list;

import list.arraylist.internal.ArrayListEx;
import list.linkedlist.internal.DoublyLinkedListEx;
import list.linkedlist.internal.SinglyLinkedListEx;

public final class Lists {
	private Lists() {}

	public static <T> MyList<T> singly() { return new SinglyLinkedListEx<>(); }
	public static <T> MyList<T> doubly() { return new DoublyLinkedListEx<>(); }
	public static <T> MyList<T> array() { return new ArrayListEx<>(); }
}
