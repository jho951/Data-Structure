package nonLinear.examples;

import nonLinear.tree.Position;
import nonLinear.tree.binary.BinaryTrees;
import nonLinear.tree.binary.MyBinaryTree;

public class BinaryTreeExample {
	public static void main(String[] args) {
		MyBinaryTree<Integer> testTree = new BinaryTrees<>();
		testTree.addRoot(1);
		Position<Integer> top = testTree.root();
		Position<Integer> b = testTree.addLeft(top,2);
		Position<Integer> c = testTree.addRight(top,3);
		testTree.addLeft(b,4);
		testTree.addRight(b,5);
		testTree.addLeft(c,6);
		testTree.addRight(c,7);
		for (int v : testTree.preorderIterable()) {
			System.out.print(v + " ");
		}
		System.out.println();

		for (int v : testTree.inorderIterable()) {
			System.out.print(v + " ");
		}
		System.out.println();

		for (int v : testTree.postorderIterable()) {
			System.out.print(v + " ");
		}
		System.out.println();
	}
}
