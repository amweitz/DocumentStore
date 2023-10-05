package edu.yu.cs.com1320.project.stage5.impl;

import org.junit.jupiter.api.Test;
import java.net.URI;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DocumentImplTest {
    @Test
    void TestDocNullUri() {
        String txt = "a";
        assertThrows(IllegalArgumentException.class, () -> {
            DocumentImpl x = new DocumentImpl(null, txt, null);
        });
    }
    @Test
    void TestDocByteNullUri() {
        byte[] x = new byte[2];
        x[0] = (byte) 3;
        x[1] = (byte) 7;
        assertThrows(IllegalArgumentException.class, () -> {
            DocumentImpl y = new DocumentImpl(null, x);
        });
    }

    @Test
    void GetDocTxt() {
        DocumentImpl x = new DocumentImpl(URI.create("A"), "b", null);
        String test = x.getDocumentTxt();
        assertEquals("b", test, "test should equal b");
    }
    @Test
    void GetDocUri() {
        DocumentImpl x = new DocumentImpl(URI.create("A"), "b", null);
        URI test = x.getKey();
        assertEquals(URI.create("A"), test, "test should equal the URI");
    }
    @Test
    void NullEquals() {
        DocumentImpl x = new DocumentImpl(URI.create("A"), "b", null);
        boolean test = x.equals(null);
        assertEquals(false, test, "test should be false");
    }
    @Test
    void setAndGetTime() {
        DocumentImpl x = new DocumentImpl(URI.create("A"), "b", null);
        x.setLastUseTime(0);
        long test = x.getLastUseTime();
        assertEquals(0, test);
    }
    @Test
    void TestDocNullCompareTo() {
        DocumentImpl x = new DocumentImpl(URI.create("A"), "b", null);
        assertThrows(NullPointerException.class, () -> {
            x.compareTo(null);
        });
    }
    @Test
    void TestDocCompareTo() {
        DocumentImpl x = new DocumentImpl(URI.create("A"), "b", null);
        x.setLastUseTime(0);
        DocumentImpl y = new DocumentImpl(URI.create("b"), "c", null);
        y.setLastUseTime(0);
        assertEquals(0, x.compareTo(y));
        y.setLastUseTime(1);
        assertEquals(-1, x.compareTo(y));
        x.setLastUseTime(2);
        assertEquals(1, x.compareTo(y));
    }

    @Test
    void GetDocByte() {
        byte[] x = new byte[2];
        x[0] = (byte) 3;
        x[1] = (byte) 7;
        DocumentImpl test = new DocumentImpl(URI.create("A"), x);
        byte[] y = test.getDocumentBinaryData();
        assertEquals(x, y, "the byte[] does not equal the getbyte method");

    }

    @Test
    void testEquals() {
        DocumentImpl x = new DocumentImpl(URI.create("A"), "b", null);
        DocumentImpl y = new DocumentImpl(URI.create("A"), "b", null);
        boolean test = x.equals(y);
        assertEquals(true, test, "test should be true");
    }

    @Test
    void GetDocTxtWordCount() {
        DocumentImpl x = new DocumentImpl(URI.create("A"), "Hello my name is Avi Weitz and I like mangos, my name is Avi", null);
        int z = x.wordCount("Avi");
        assertEquals(2, z, "Avi should have appeared twice");
    }
    @Test
    void GetDocTxtWordCountzero() {
        DocumentImpl x = new DocumentImpl(URI.create("A"), "Hello my name is Avi Weitz and I like mangos, my name is Avi", null);
        int z = x.wordCount("p");
        assertEquals(0, z, "p appears 0 times");
    }

    @Test
    void GetDocTxtPrefixCount() {
        DocumentImpl x = new DocumentImpl(URI.create("A"), "Any Avi As Always ami 123", null);
        int z = x.wordCount("A");
        assertEquals(4, z, "the prefix should have appeared four times");
    }
    @Test
    void GetDocWords() {
        DocumentImpl x = new DocumentImpl(URI.create("A"), "Any Avi As Always ami 123", null);
        Set < String > z = x.getWords();
        System.out.println(z);
    }

    @Test
    void GetDocWordswithoutspecialchars() {
        DocumentImpl x = new DocumentImpl(URI.create("A"), "Any];[] Avi*^%^% As%%^ Always.'. ami&%^% 123)((", null);
        Set < String > z = x.getWords();
        System.out.println(z);
    }
    @Test
    void GetDocWordsblank() {
        DocumentImpl x = new DocumentImpl(URI.create("A"), "&", null);
        Set < String > z = x.getWords();
        System.out.println(z);
    }

    @Test
    void testBinaryGetWordCount() {
        byte[] x = new byte[2];
        x[0] = (byte) 3;
        x[1] = (byte) 7;
        DocumentImpl test = new DocumentImpl(URI.create("A"), x);
        int z = test.wordCount("test");
        assertEquals(0, z, "its a binary data which means the count should be zero");
    }

}