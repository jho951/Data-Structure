package LinkedList;

public class Node {
	int data;
	Node next = null; // 해당하는 node 클래스의 주소를 바라보고 있음

	Node(int data) {
		this.data = data;
	}

	void append(int data) {
		Node end = new Node(data);
		Node n = this;
		while (n.next != null) {
			n = n.next;
		}
		n.next = end;
	}

	void delete() {
		Node n = this;
		while (n.next != null) {
			if(n.next.data == data){
				n.next = n.next.next;
			}else{
				n = n.next;
			}
		}
	}

	void retrieve() {
		Node n = this;
		while (n.next != null) {
			System.out.print(n.data + " -> ");
			n = n.next;
		}
		System.out.println(n.data);
	}

}
