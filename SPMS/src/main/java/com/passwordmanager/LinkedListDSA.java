package com.passwordmanager;

public class LinkedListDSA<T> {

    // Inner class representing a node in the linked list
    private class Node {
        T data;
        Node next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head;
    private int size;

    // Constructor
    public LinkedListDSA() {
        this.head = null;
        this.size = 0;
    }

    // Add an element at the end of the list
    public void add(T data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    // Get an element by its index
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    // Remove an element by its index
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }

        Node current = head;
        if (index == 0) {
            head = head.next;
        } else {
            Node prev = null;
            for (int i = 0; i < index; i++) {
                prev = current;
                current = current.next;
            }
            if (prev != null) {
                prev.next = current.next;
            }
        }
        size--;
        return current.data;
    }

    // Get the size of the list
    public int size() {
        return size;
    }

    // Check if the list is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Clear the list
    public void clear() {
        head = null;
        size = 0;
    }
}
