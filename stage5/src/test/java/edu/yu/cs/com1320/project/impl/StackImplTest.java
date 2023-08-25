package edu.yu.cs.com1320.project.impl;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class StackImplTest {
    @Test
    void TestStackPush() {
        StackImpl<String> test = new StackImpl<>();
        test.push("AVI");
        String x = test.pop();
        assertEquals("AVI", x,"x should be Avi");
    }

    @Test
    void TestStackPushTwice() {
        StackImpl<String> test = new StackImpl<>();
        test.push("AVI");
        test.push("WEITZ");
        String x = test.pop();
        assertEquals("WEITZ", x,"x should be WEITZ");
    }
    @Test
    void TestStackPeek() {
        StackImpl<String> test = new StackImpl<>();
        test.push("AVI");
        String x = test.peek();
        assertEquals("AVI", x,"x should be AVI");
    }

    @Test
    void TestStackSize() {
        StackImpl<String> test = new StackImpl<>();
        test.push("AVI");
        test.push("AVI");
        test.push("AVI");
        test.push("AVI");
        int x = test.size();
        assertEquals(4, x,"length should be 4");
    }
    @Test
    void TestPopNull() {
        StackImpl<String> test = new StackImpl<>();
        String x = test.pop();
        assertEquals(null, x,"should be null");
    }

}