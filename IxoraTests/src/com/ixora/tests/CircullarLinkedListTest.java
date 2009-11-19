/**
 * 01-Mar-2006
 */
package com.ixora.tests;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import com.ixora.common.collections.CircullarLinkedList;

/**
 * @author Daniel Moraru
 */
public class CircullarLinkedListTest extends TestCase {
	public void testAdd() {
		CircullarLinkedList<Integer> lst = new CircullarLinkedList<Integer>(3);
		lst.add(1);
		lst.add(2);
		lst.add(3);
		lst.add(4);
		lst.add(5);
		printList(lst);

		lst.clear();

		List<Integer> lst2 = new LinkedList<Integer>();
		lst2.add(1);
		lst2.add(2);
		lst2.add(3);
		lst2.add(4);
		lst2.add(5);
		lst.addAll(lst2);
		printList(lst);

		lst.clear();

		lst.add(1);
		lst.add(2);
		lst.add(3);
		lst.add(2, 4);
		printList(lst);

		lst.add(1);
		lst.add(2);
		lst.add(3);
		lst.addAll(2, lst2);
		printList(lst);
	}

	private void printList(Collection<?> coll) {
		System.out.println("------------");
		for(Object obj : coll) {
			System.out.println(obj.toString() + ":");
		}
		System.out.println("------------");
	}
}
