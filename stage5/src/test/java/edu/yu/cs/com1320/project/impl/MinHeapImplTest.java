package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import org.junit.jupiter.api.Test;
import edu.yu.cs.com1320.project.stage5.Document;
import java.net.URI;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;
class MinHeapImplTest {
    @Test
    void testInsertAndDelete() {
        MinHeapImpl<Integer> heap = new MinHeapImpl<>();
        int a = 1;
        int b = 2;
        int c = 3;
        int d = 4;
        int e = 5;
        int f = 6;
        heap.insert(a);
        heap.insert(b);
        heap.insert(c);
        heap.insert(d);
        heap.insert(e);
        heap.insert(f);
        int x = (int) heap.remove();
        assertEquals(1,x,"should return 1");
    }

    @Test
    void testReheapfy() {
        MinHeapImpl<Document> heap = new MinHeapImpl<>();
        DocumentImpl x = new DocumentImpl(URI.create("A"),"b",null);
        x.setLastUseTime(System.nanoTime());
        heap.insert(x);
        DocumentImpl y = new DocumentImpl(URI.create("R"),"c",null);
        y.setLastUseTime(System.nanoTime());
        heap.insert(y);
        y.setLastUseTime(0); //preparing y to be pushed up the heap
        heap.reHeapify(y); //pushing y up the heap
        Document z = (Document) heap.remove();
        assertEquals(y,z,"should have pushed the document up the heap");
    }
    @Test
    void testGetArrayIndexTwo() {
        MinHeapImpl<Document> heap = new MinHeapImpl<>();
        DocumentImpl x = new DocumentImpl(URI.create("A"),"b",null);
        x.setLastUseTime(System.nanoTime());
        heap.insert(x);assertThrows(NoSuchElementException.class, () -> {
            heap.getArrayIndex(null);
        });
    }
    @Test
    void testReheapify() {
        MinHeapImpl<Document> heap = new MinHeapImpl<>();
        DocumentImpl x = new DocumentImpl(URI.create("A"),"b",null);
        x.setLastUseTime(System.nanoTime());
        heap.insert(x);assertThrows(NoSuchElementException.class, () -> {
            heap.reHeapify(null);
        });
    }
    @Test
    void testNoSuchElementException() {
        MinHeapImpl<Integer> heap = new MinHeapImpl<>();
        heap.insert(1);
        assertThrows(NoSuchElementException.class, () -> {
            heap.getArrayIndex(2);
        });
    }

}