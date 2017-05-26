package editor;

public class LinkedListText <Item> {
	

	public class Node {
		public Item item;     /* Equivalent of first */
		public Node next; /* Equivalent of rest */
		public Node previous;
		public Node(Node p, Item i, Node h) {
			previous = p;
			item = i;
			next = h;		
		}
	}

	private Node sent;
	public Node cursor;
	private int length;

	public LinkedListText()  {

		length = 0;
		sent = new Node(null, null, null);
		cursor = new Node(null, null, null);
		sent.previous = sent;
		sent.next = sent;
		cursor.previous = sent;
		cursor.next = sent;
	}


	public void addFirst (Item y) {

		Node oldFront = sent.next;
		Node newFront = new Node(sent, y, oldFront);
		oldFront.previous = newFront;
		sent.next = newFront;
		length += 1;
	}	


	public void addLast (Item y) {

		length += 1;

		Node oldLast = cursor.previous;
		Node newLast = new Node(oldLast, y, cursor);
		oldLast.next = newLast;
		cursor.previous = newLast;
	}	

	public boolean isEmpty() {
		if (sent.next == sent) {
			return true;
		}
		else	{

			return false;
		}

	}

	public int size() {

		return length;

	}

	public Item removeFirst() {

		if (sent.next == null) {

			return null;
		}
		else {
			Node itemToRemove = sent.next;
			Node newFirst = itemToRemove.next;
			sent.next = newFirst;
			newFirst.previous = sent;
			length -= 1;
			return itemToRemove.item;
		}

	}

	public Item removeLast() {
		
		if (cursor.previous == null) {
			return null;
		}
		else {
			Node itemToRemove = cursor.previous;
			Node newLast = itemToRemove.previous;
			cursor.previous = newLast;
			newLast.next = cursor;
			length -= 1;
			return itemToRemove.item;
		}
	}

	public Item get(int index) {

		int counter = 0;
		Node itemToGet = sent.next;
		if (index == 0)	{
			return itemToGet.item;
		}

		else	{

			while (counter < index) {

				itemToGet = itemToGet.next;
				counter += 1;
			}

			return itemToGet.item;
		}

	}

	public Node getCursor() { 
		return cursor; 
	}

	public void moveCursorRight() {
		Node temp = cursor.next;
		temp.previous = cursor.previous;
		cursor.next = cursor.next.next;
		cursor.previous.next = temp;
		cursor.previous = temp;
		temp.next = cursor;


	}

	public void moveCursorLeft() {
		Node temp = cursor.previous;
		temp.next = cursor.next;
		cursor.previous = cursor.previous.previous;
		cursor.next = temp;
		cursor.previous.next = cursor;
		temp.previous = cursor;


	}

	public void detachAndAttach(Node input) {
		Node temp = cursor.next;
		cursor.previous.next = temp;
		cursor.next.previous = cursor.previous;
		cursor.previous = input;
		input.next.previous = cursor;
		cursor.next = input.next;
		input.next = cursor;
	}

	public Node getNode(int index) {
		int counter = 0;
		Node itemToGet = sent.next;
		if (index == 0)	{
			return itemToGet;
		}
		else {
			while (counter < index) {
				itemToGet = itemToGet.next;
				counter += 1;
			}
			return itemToGet;
		}
	}

	public Node getPrevious() {
		return cursor.previous;
	}

	public Node getNext() {
		return cursor.next;
	}

	public Item getItemPrevious() {
		return cursor.previous.item;
	}

	public Item getItemNext() {
		return cursor.next.item;
	}

	public Item getItemCursor() {
		return cursor.item;
	}

	// public Item getItem() {
	// 	return Node.item;
	// }

	public void printDeque() {

		int counter = 0;
		while (counter < length) {
			System.out.println(sent.next.item + " ");
			counter += 1;
			sent = sent.next;
		}
	}

}