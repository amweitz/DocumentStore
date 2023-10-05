package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.MinHeap;

import java.util.NoSuchElementException;

public class MinHeapImpl < E extends Comparable < E >> extends MinHeap < E > {

    public MinHeapImpl() {
        this.elements = (E[]) new Comparable[5];

    }
    @Override
    public void reHeapify(E element) {
        int index = getArrayIndex(element);
        this.upHeap(index);
        this.downHeap(index);
    }
    //locate the Index of an element. If no such index exists throw a NoSuchElementException
    @Override
    protected int getArrayIndex(E element) {
        if (element == null) {
            throw new NoSuchElementException();
        }
        int index = -1;
        for (int x = 0; x < this.elements.length; x++) {
            if (element.equals(this.elements[x])) {
                index = x;
            }
        }
        if (index == -1) {
            throw new NoSuchElementException();
        }
        return index;
    }
    @Override
    protected void doubleArraySize() {
        E[] temp = (E[]) new Comparable[this.elements.length * 2];
        for (int x = 0; x < this.elements.length; x++) {
            temp[x] = (E) this.elements[x];
        }
        this.elements = temp;
    }
}