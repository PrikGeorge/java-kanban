package manager;

public class Node<T> {

    private final T elem;

    private Node<T> next;

    private Node<T> prev;

    public Node(T task) {
        this.elem = task;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    public T getElem() {
        return elem;
    }
}
