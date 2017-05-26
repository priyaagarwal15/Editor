package editor;

public class LinkedListDeque <Item> {
	

	private class LinkedList {
		public Item item;     /* Equivalent of first */
		public LinkedList next; /* Equivalent of rest */
		public LinkedList previous;
		public LinkedList(LinkedList p, Item i, LinkedList h) {
			previous = p;
			item = i;
			next = h;		
		}
	}

	private LinkedList sent;
	private int length;

		public LinkedListDeque()  {

			length = 0;
			sent = new LinkedList(null, null, null);
			sent.previous = sent;
			sent.next = sent;
		}
	

		public void addFirst (Item y) {

			LinkedList oldFront = sent.next;
			LinkedList newFront = new LinkedList(sent, y, oldFront);
			oldFront.previous = newFront;
			sent.next = newFront;
			length += 1;
		}	


		public void addLast (Item y) {

			length += 1;
			LinkedList oldLast = sent.previous;
			LinkedList newLast = new LinkedList(oldLast, y, sent);
			oldLast.next = newLast;
			sent.previous = newLast;
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


		public void printDeque() {

			int counter = 0;
			while (counter < length) {
				System.out.println(sent.next.item + " ");
				counter += 1;
				sent = sent.next;
			}
		}

		public Item removeFirst() {

			if (sent.next == null) {

				return null;
			}
			else {
				LinkedList itemToRemove = sent.next;
				LinkedList newFirst = itemToRemove.next;
				sent.next = newFirst;
				newFirst.previous = sent;
				length -= 1;
				return itemToRemove.item;
			}

		}

		public Item removeLast() {
			
			if (sent.previous == null) {

				return null;
			}

			else {
				LinkedList itemToRemove = sent.previous;
				LinkedList newLast = itemToRemove.previous;
				sent.previous = newLast;
				newLast.next = sent;
				length -= 1;

				return itemToRemove.item;
			}
		}



		public Item get(int index) {

			int counter = 0;
			LinkedList itemToGet = sent.next;
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

		private Item getHelper (int index, LinkedList position)	{

			if (index == 0)	{
				return position.item;
			}
			else	{
				return getHelper( index - 1, position.next);
			}
		}

		
		public Item getRecursive(int index) {
			LinkedList position = sent.next;
			return getHelper(index, position);
		}
}