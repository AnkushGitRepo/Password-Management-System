package com.passwordmanager;

public class StackDSA<T> {

    private T[] stackArray;
    private int top;
    private int capacity;

    // Constructor
    @SuppressWarnings("unchecked")
    public StackDSA(int capacity) {
        this.capacity = capacity;
        this.stackArray = (T[]) new Object[capacity];
        this.top = -1;
    }

    // Push an element onto the stack
    public void push(T data) {
        if (isFull()) {
            throw new StackOverflowError("Stack is full");
        }
        stackArray[++top] = data;
    }

    // Pop an element from the stack
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return stackArray[top--];
    }

    // Peek at the top element without removing it
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return stackArray[top];
    }

    // Check if the stack is empty
    public boolean isEmpty() {
        return top == -1;
    }

    // Check if the stack is full
    public boolean isFull() {
        return top == capacity - 1;
    }

    // Get the size of the stack
    public int size() {
        return top + 1;
    }

    // Clear the stack
    public void clear() {
        top = -1;
    }
}
