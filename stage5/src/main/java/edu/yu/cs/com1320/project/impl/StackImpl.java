package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.Stack;



public class StackImpl<T> implements Stack<T>{
    T[] array;
    int count;

    public StackImpl(){
        this.array = (T[]) new Object[1];
        this.count = 0;
    }
    /**
     * @param element object to add to the Stack
     */
    @Override
    public void push(T element) {
        if (this.count == 0){
            this.array[0] = element;
            count++;
        }
        else{
            T[] temp = (T[]) new Object[this.count + 1];
            for (int x = 0; x < this.count; x++){
                temp[x] = this.array[x];
            }
            this.array = temp;
            this.array[this.count] = element;
            count++;
        }

    }
    /**
     * removes and returns element at the top of the stack
     * @return element at the top of the stack, null if the stack is empty
     */
    @Override
    public T pop() {
        if (this.count == 0){
            return null;
        }
        if (this.count == 1){
          T hold = this.array[0];
          this.array[0] = null;
          this.count--;
          return hold;
        }
        else{
            T ex = this.array[this.count - 1];
            T[] temp = (T[]) new Object[this.count - 1];
            for (int x = 0; x < this.count - 1; x++){
                temp[x] = this.array[x];
            }
            this.array = temp;
            this.count--;
            return ex;
        }
    }
    /**
     *
     * @return the element at the top of the stack without removing it
     */
    @Override
    public T peek() {
        if (this.count == 0){
            return null;
        }
        return this.array[this.count - 1];
    }
    /**
     *
     * @return how many elements are currently in the stack
     */
    @Override
    public int size() {

        return this.count;
    }
}
