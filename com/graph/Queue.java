/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.graph;

/**
 *
 * @author Epaminondas
 */
public class Queue {

    private final int SIZE = 50;
    private int[] queArray;
    private int front;
    private int rear;
// -------------------------------------------------------------

    // constructor
    public Queue() {
        queArray = new int[SIZE];
        front = 0;
        rear = -1;
    }
// -------------------------------------------------------------

    // put item at rear of queue
    public void insert(int j) {
        if (rear == SIZE - 1) {
            rear = -1;
        }
        queArray[++rear] = j;
    }
// -------------------------------------------------------------

    // take item from front of queue
    public int remove() {
        int temp = queArray[front++];
        if (front == SIZE) {
            front = 0;
        }
        return temp;
    }
// -------------------------------------------------------------

    // true if queue is empty
    public boolean isEmpty() {
        return (rear + 1 == front || (front + SIZE - 1 == rear));
    }
// -------------------------------------------------------------
} // end class Queue

