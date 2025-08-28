package nonLinear.examples;

import nonLinear.tree.Position;
import nonLinear.tree.general.GeneralTrees;
import nonLinear.tree.general.MyGeneralTree;

public class GeneralTreeExample {
	public static void main(String[] args) {
		MyGeneralTree<String> testTree = new GeneralTrees<>();
		Position<String> top = testTree.root();
		Position<String> r = testTree.addRoot("A");
		Position<String> b = testTree.addChild(r, "B");
		Position<String> c = testTree.addChild(r, "C");
		Position<String> d = testTree.addChild(b, "D");

		System.out.print("Preorder : ");
		for (String v : testTree) System.out.print(v + " ");
		System.out.println();
		System.out.println(top.element());
		System.out.println(r );
	}
}
