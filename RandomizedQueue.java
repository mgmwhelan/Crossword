/*************************************************************************
 *  Compilation:  javac RandomizedQueue.java
 *  Execution:    java RandomizedQueue
 *  
 *  Implement a randomized queue in log N time per operation in the 
 *  worst case.
 *
 *************************************************************************/

public class RandomizedQueue<Item> {

    private RedBlackBST<Integer, Item> st = new RedBlackBST<Integer, Item>();

    public RandomizedQueue() { }

    // add the item to the randomized queue
    public void enqueue(Item item) {
        int N = st.size();
        int r = StdRandom.uniform(N+1);
        st.put(N, st.get(r));
        st.put(r, item);
    }

    // delete and return a random item from the queue
    public Item dequeue() {
        int N = st.size();
        if (N == 0) throw new RuntimeException("Randomized queue underflow");
        Item item = st.get(N-1);
        st.delete(N-1);
        return item;
    }


   /*************************************************************************
    *  Test client
    *************************************************************************/
    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        int k = Integer.parseInt(args[1]);
        RandomizedQueue<Integer> queue = new RandomizedQueue<Integer>();
        for (int i = 0; i < N; i++)
            queue.enqueue(i);

        for (int i = 0; i < k; i++)
            System.out.println(queue.dequeue());
    }
}
