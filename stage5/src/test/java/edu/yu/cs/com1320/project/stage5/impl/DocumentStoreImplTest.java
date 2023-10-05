package edu.yu.cs.com1320.project.stage5.impl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
class DocumentStoreImplTest {
    @Test
    void testNullUri() {
        byte[] y = new byte[2];
        y[0] = (byte) 20;
        y[1] = (byte) 25;
        InputStream isTest = new ByteArrayInputStream(y);
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.BINARY;
        DocumentStoreImpl x = new DocumentStoreImpl();
        assertThrows(IllegalArgumentException.class, () -> {
            x.put(isTest, null, format);
        });
    }
    @Test
    void testNullFormat() {
        byte[] y = new byte[2];
        y[0] = (byte) 20;
        y[1] = (byte) 25;
        InputStream isTest = new ByteArrayInputStream(y);
        DocumentStoreImpl x = new DocumentStoreImpl();
        assertThrows(IllegalArgumentException.class, () -> {
            x.put(isTest, URI.create("a"), null);
        });
    }
    @Test
    void testPut() throws IOException {
        byte[] y = new byte[2];
        y[0] = (byte) 20;
        y[1] = (byte) 25;
        InputStream isTest = new ByteArrayInputStream(y);
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        int num = test.put(isTest, x, format);
        assertEquals(0, num, "Should return 0");
    }
    @Test
    void testNewPut() throws IOException {
        byte[] y = new byte[2];
        y[0] = (byte) 20;
        y[1] = (byte) 25;
        InputStream isTest = new ByteArrayInputStream(y);
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.BINARY;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        int hash = test.get(x).hashCode();
        byte[] z = new byte[2];
        z[0] = (byte) 90;
        z[1] = (byte) 46;
        InputStream ts = new ByteArrayInputStream(z);
        int val = test.put(ts, x, format);
        assertEquals(hash, val, "should return the hashcode");
    }
    @Test
    void testget() throws IOException {
        byte[] y = new byte[2];
        y[0] = (byte) 20;
        y[1] = (byte) 25;
        InputStream isTest = new ByteArrayInputStream(y);
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.BINARY;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        Document s = test.get(x);
        DocumentImpl w = new DocumentImpl(x, y);
        boolean t = s.equals(w);
        assertEquals(true, t, "T should be true");
    }
    @Test
    void testDelete() throws IOException {
        byte[] y = new byte[2];
        y[0] = (byte) 20;
        y[1] = (byte) 25;
        InputStream isTest = new ByteArrayInputStream(y);
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.BINARY;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        boolean t = test.delete(x);
        assertEquals(true, t, "Should have deleted mapping");
    }
    @Test
    void testundoNewPut() throws IOException {
        byte[] y = new byte[2];
        y[0] = (byte) 20;
        y[1] = (byte) 25;
        InputStream isTest = new ByteArrayInputStream(y);
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.BINARY;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        test.undo();
        byte[] p = new byte[2];
        p[0] = (byte) 2;
        p[1] = (byte) 25;
        InputStream nt = new ByteArrayInputStream(p);
        DocumentStore.DocumentFormat ft = DocumentStore.DocumentFormat.BINARY;
        URI uri = URI.create("a");
        int z = test.put(nt, uri, ft);
        assertEquals(0, z, "Should be zero");
    }
    @Test
    void testundoModifyPut() throws IOException {
        byte[] y = new byte[2];
        y[0] = (byte) 20;
        y[1] = (byte) 25;
        InputStream isTest = new ByteArrayInputStream(y);
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.BINARY;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        Document doc = test.get(x);
        byte[] p = new byte[2];
        p[0] = (byte) 2;
        p[1] = (byte) 25;
        InputStream nt = new ByteArrayInputStream(p);
        DocumentStore.DocumentFormat ft = DocumentStore.DocumentFormat.BINARY;
        test.put(nt, x, ft);
        test.undo();
        Document dc = test.get(x);
        assertEquals(doc, dc, "Should have reverted back to old document");
    }
    @Test
    void testundoDelete() throws IOException {
        byte[] y = new byte[2];
        y[0] = (byte) 20;
        y[1] = (byte) 25;
        InputStream isTest = new ByteArrayInputStream(y);
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.BINARY;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        Document doc = test.get(x);
        test.delete(x);
        test.undo();
        Document dc = test.get(x);
        assertEquals(doc, dc, "Should have reverted back to old document");
    }

    @Test
    void testUndoUri() throws IOException {
        byte[] y = new byte[2];
        y[0] = (byte) 20;
        y[1] = (byte) 25;
        InputStream isTest = new ByteArrayInputStream(y);
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.BINARY;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        byte[] z = new byte[2];
        z[0] = (byte) 90;
        z[1] = (byte) 46;
        InputStream ts = new ByteArrayInputStream(z);
        URI uri = URI.create("b");
        test.put(ts, uri, format);
        test.undo(x);
        byte[] p = new byte[2];
        p[0] = (byte) 20;
        p[1] = (byte) 25;
        InputStream is = new ByteArrayInputStream(p);
        URI b = URI.create("a");
        int r = test.put(is, b, format);
        assertEquals(0, r, "Should have be zero");
    }
    @Test
    void testUndoUriMulti() throws IOException {
        byte[] y = new byte[2];
        y[0] = (byte) 20;
        y[1] = (byte) 25;
        InputStream isTest = new ByteArrayInputStream(y);
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.BINARY;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        byte[] z = new byte[2];
        z[0] = (byte) 90;
        z[1] = (byte) 46;
        InputStream ts = new ByteArrayInputStream(z);
        URI uri = URI.create("b");
        test.put(ts, uri, format);
        byte[] p = new byte[2];
        p[0] = (byte) 20;
        p[1] = (byte) 25;
        InputStream is = new ByteArrayInputStream(p);
        URI b = URI.create("c");
        test.put(is, b, format);
        test.undo(x);
        assertEquals(null, test.get(x));
        test.undo();
        assertEquals(null, test.get(b));
    }
    @Test
    void testUndoUriMultiTxt() throws IOException {
        String a = "Hi my name is Avi Weitz";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        String z = "Avi Weitz";
        InputStream ts = new ByteArrayInputStream(z.getBytes());
        URI uri = URI.create("b");
        test.put(ts, uri, format);
        String y = "Weitz";
        InputStream is = new ByteArrayInputStream(y.getBytes());
        URI b = URI.create("c");
        test.put(is, b, format);
        test.undo(x);
        assertEquals(null, test.get(x));
        test.undo();
        assertEquals(null, test.get(b));
    }
    @Test
    void testPutNullInput() throws IOException {
        byte[] y = new byte[2];
        y[0] = (byte) 20;
        y[1] = (byte) 25;
        InputStream isTest = new ByteArrayInputStream(y);
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        int z = test.get(x).hashCode();
        int t = test.put(null, x, format);
        assertEquals(t, z, "Should have same hashcode");
    }
    @Test
    void testEmptyStackUndo() {
        DocumentStoreImpl x = new DocumentStoreImpl();
        assertThrows(IllegalStateException.class, () -> {
            x.undo();
        });
    }
    @Test
    void testNullDelete() throws IOException {
        DocumentStoreImpl test = new DocumentStoreImpl();
        boolean x = test.delete(URI.create("a"));
        assertEquals(false, x, "Should return false");

    }
    @Test
    void testEmptyStackUndoUri() {
        DocumentStoreImpl x = new DocumentStoreImpl();
        assertThrows(IllegalStateException.class, () -> {
            x.undo(URI.create("a"));
        });
    }
    @Test
    void testPutNullInputNoUri() throws IOException {
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        int t = test.put(null, x, format);
        assertEquals(0, t, "Should have returned 0");
    }
    @Test
    void testPuttxt() throws IOException {
        String a = "Hi my name is Avi Weitz";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        int num = test.put(isTest, x, format);
        assertEquals(0, num, "Should return 0");
    }
    @Test
    void testserchtxt() throws IOException {
        String a = "Hi my name is Avi Weitz Avi Avi";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        String b = "Avi Weitz Avi";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        DocumentStoreImpl doc = new DocumentStoreImpl();
        test.put(tst, uri, format);
        List < Document > documents = test.search("Avi");
        List < Document > docs = new ArrayList < > ();
        docs.add(test.get(x));
        docs.add(test.get(uri));
        assertEquals(docs, documents);
    }
    @Test
    void testserchpre() throws IOException {
        String a = "Hi my name is Avi Weitz Avi Avi";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        String b = "Avi Weitz Avi";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        String c = "A Av Avi AViw Avi";
        InputStream ts = new ByteArrayInputStream(c.getBytes());
        URI u = URI.create("c");
        test.put(ts, u, format);
        String d = "a av avi aviw";
        InputStream t = new ByteArrayInputStream(d.getBytes());
        URI i = URI.create("d");
        test.put(t, i, format);
        List < Document > documents = test.searchByPrefix("A");
        List < Document > docs = new ArrayList < > ();
        docs.add(test.get(u));
        docs.add(test.get(x));
        docs.add(test.get(uri));
        assertEquals(docs, documents);
    }
    @Test
    void deletestringpre() throws IOException {
        String a = "Hi my name is Avi Weitz Avi Avi";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        String b = "avi weitz avi";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        Set < URI > urs = test.deleteAllWithPrefix("w");
        Set < URI > uris = new HashSet < > ();
        uris.add(uri);
        assertEquals(urs, uris);
    }
    @Test
    void testdeletestringpreundo() throws IOException {
        String a = "Hi my name is Avi Weitz Avi Avi";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        String b = "avi weitz avi";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        test.deleteAllWithPrefix("w");
        List < Document > urs = new ArrayList < > ();
        assertEquals(urs, test.searchByPrefix("w"));
        test.undo();
        urs.add(test.get(uri));
        assertEquals(urs, test.searchByPrefix("w"));
    }
    @Test
    void testdeletestringundo() throws IOException {
        String a = "Hi my name is Avi Weitz Avi Avi";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        String b = "Avi weitz avi";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        String c = "avi weitz avi";
        InputStream ts = new ByteArrayInputStream(c.getBytes());
        URI u = URI.create("c");
        test.put(ts, u, format);
        test.deleteAll("Avi");
        List < Document > urs = new ArrayList < > ();
        assertEquals(urs, test.search("Avi"));
        test.undo();
        urs.add(test.get(x));
        urs.add(test.get(uri));
        assertEquals(urs, test.search("Avi"));
    }
    @Test
    void testDeleteStringpreUndoUri() throws IOException {
        String a = "Hi my name is Avi weitz Avi Avi";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        String b = "avi weitz avi";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        test.deleteAllWithPrefix("a");
        List < Document > urs = new ArrayList < > ();
        assertEquals(urs, test.searchByPrefix("a"));
        test.undo(uri);
        urs.add(test.get(uri));
        assertEquals(urs, test.searchByPrefix("a"));
    }
    @Test
    void testDeleteStringpreUndoUriMult() throws IOException {
        String a = "Hi my name is Avi weitz Avi Avi";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        String b = "avi weitz avi";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        test.deleteAllWithPrefix("w");
        List < Document > urs = new ArrayList < > ();
        assertEquals(urs, test.searchByPrefix("w"));
        test.undo(uri);
        urs.add(test.get(uri));
        assertEquals(urs, test.searchByPrefix("a"));
    }
    @Test
    void testDeleteStringpreUndoUriMultStack() throws IOException {
        String a = "Hi my name is Avi weitz Avi Avi";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        String b = "avi weitz avi";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        test.deleteAllWithPrefix("w");
        List < Document > urs = new ArrayList < > ();
        assertEquals(urs, test.searchByPrefix("w"));
        String c = "comp sci";
        InputStream cs = new ByteArrayInputStream(c.getBytes());
        URI q = URI.create("p");
        test.put(cs, q, format);
        test.undo(uri);
        urs.add(test.get(uri));
        assertEquals(urs, test.searchByPrefix("a"));
        test.undo();
        String d = "comp sci";
        InputStream wymore = new ByteArrayInputStream(d.getBytes());
        URI r = URI.create("p");
        int num = test.put(wymore, r, format);
        assertEquals(0, num);
    }
    @Test
    void testDeleteTwo() throws IOException {
        String y = "comp sci";
        InputStream isTest = new ByteArrayInputStream(y.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        test.delete(x);
        test.undo();
    }
    @Test
    void testPutHeapMemoryDocAboveLimit() throws IOException { //above doc limit
        String a = "Hi my name is Avi Weitz Avi Avi";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        String b = "Avi Weitz Avi";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        String c = "A Av Avi AViw Avi";
        InputStream ts = new ByteArrayInputStream(c.getBytes());
        URI u = URI.create("c");
        test.put(ts, u, format);
        String d = "a av avi aviw";
        InputStream t = new ByteArrayInputStream(d.getBytes());
        URI i = URI.create("d");
        test.put(t, i, format);
        test.setMaxDocumentCount(3); //setting a limit which is higher then account - should delete top of heap one
        File file = getFile(x);
        assertTrue(file.exists());
        test.get(x);
        assertFalse(file.exists());
        File tf = getFile(uri);
        assertTrue(tf.exists());
    }
    @Test
    void testPutHeapMemoryAboveByteLimit() throws IOException { //above byte limit
        String a = "Hi my name is Avi Weitz Avi Avi";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        String b = "Avi Weitz Avi";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        String c = "A Av Avi AViw Avi";
        InputStream ts = new ByteArrayInputStream(c.getBytes());
        URI u = URI.create("c");
        test.put(ts, u, format);
        String d = "a av avi aviw";
        InputStream t = new ByteArrayInputStream(d.getBytes());
        URI i = URI.create("d");
        test.put(t, i, format);
        test.setMaxDocumentBytes(73); //we currently have 74 byte so it should delete the top of heap
        File file = getFile(x);
        assertTrue(file.exists());
        test.get(x);
        assertFalse(file.exists());
        File tf = getFile(uri);
        assertTrue(tf.exists());
    }
    @Test
    void testAddDocAboveLimit() throws IOException { //doc we put in is above the limit
        String a = "Hi my name is Avi Weitz Avi Avi";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.setMaxDocumentCount(3); //setting a limit
        test.put(isTest, x, format);
        String b = "Avi Weitz Avi";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        String c = "A Av Avi AViw Avi";
        InputStream ts = new ByteArrayInputStream(c.getBytes());
        URI u = URI.create("c");
        test.put(ts, u, format);
        String d = "a av avi aviw";
        InputStream t = new ByteArrayInputStream(d.getBytes());
        URI i = URI.create("d");
        test.put(t, i, format);
        File file = getFile(x);
        assertTrue(file.exists());
        test.get(x);
        assertFalse(file.exists());
        File tf = getFile(uri);
        assertTrue(tf.exists());
    }
    @Test
    void testAddDocAboveByteLimit() throws IOException { //doc we put in is above the limit
        String a = "Hi my name is Avi Weitz Avi Avi";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.setMaxDocumentBytes(73); //setting a limit
        test.put(isTest, x, format);
        String b = "Avi Weitz Avi";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        String c = "A Av Avi AViw Avi";
        InputStream ts = new ByteArrayInputStream(c.getBytes());
        URI u = URI.create("c");
        test.put(ts, u, format);
        String d = "a av avi aviw";
        InputStream t = new ByteArrayInputStream(d.getBytes());
        URI i = URI.create("d");
        test.put(t, i, format);
        File file = getFile(x);
        assertTrue(file.exists());
        test.get(x);
        assertFalse(file.exists());
        File tf = getFile(uri);
        assertTrue(tf.exists());
    }
    @Test
    void testAddDocAboveBothLimits() throws IOException {
        String a = "Hi my name is Avi Weitz Avi Avi";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.setMaxDocumentBytes(150); //setting a limit
        test.put(isTest, x, format);
        String b = "Avi Weitz Avi";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        String c = "A Av Avi AViw Avi";
        InputStream ts = new ByteArrayInputStream(c.getBytes());
        URI u = URI.create("c");
        test.put(ts, u, format);
        test.setMaxDocumentCount(3);
        String d = "a av avi aviw";
        InputStream t = new ByteArrayInputStream(d.getBytes());
        URI i = URI.create("d");
        test.put(t, i, format);
        File file = getFile(x);
        assertTrue(file.exists());
    }
    @Test
    void testUpdateHeapifyOnGet() throws IOException {
        String a = "Hi my name is Avi Weitz Avi Avi";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.setMaxDocumentBytes(73); //setting a limit
        test.put(isTest, x, format);
        String b = "Avi Weitz Avi";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        String c = "A Av Avi AViw Avi";
        InputStream ts = new ByteArrayInputStream(c.getBytes());
        URI u = URI.create("c");
        test.put(ts, u, format);
        test.get(x);
        String d = "a av avi aviw";
        InputStream t = new ByteArrayInputStream(d.getBytes());
        URI i = URI.create("d");
        test.put(t, i, format);
        File file = getFile(uri);
        assertTrue(file.exists());
    }
    @Test
    void testUpdateHeapifyOnSearch() throws IOException {
        String a = "abc";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.setMaxDocumentCount(3); //setting a limit
        test.put(isTest, x, format);
        String b = "abd abc";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        String c = "efg";
        InputStream ts = new ByteArrayInputStream(c.getBytes());
        URI u = URI.create("c");
        test.put(ts, u, format);
        test.search("abc");
        String d = "hij";
        InputStream t = new ByteArrayInputStream(d.getBytes());
        URI i = URI.create("d");
        test.put(t, i, format);
        File file = getFile(u);
        assertTrue(file.exists());
    }
    @Test
    void testUpdateHeapifyOnSearchPre() throws IOException {
        String a = "Hi my name is Avi Weitz Avi Avi";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.setMaxDocumentCount(3); //setting a limit
        test.put(isTest, x, format);
        String b = "Avi Weitz Avi";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        String c = "a av avi aViw avi";
        InputStream ts = new ByteArrayInputStream(c.getBytes());
        URI u = URI.create("c");
        test.put(ts, u, format);
        test.searchByPrefix("A");
        String d = "a av avi aviw";
        InputStream t = new ByteArrayInputStream(d.getBytes());
        URI i = URI.create("d");
        test.put(t, i, format);
        File file = getFile(u);
        assertTrue(file.exists());
    }
    @Test
    void testUndoOverLimit() throws IOException {
        String a = "Hi my name is Avi Weitz Avi Avi";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        String b = "Avi Weitz Avi";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        test.delete(x);
        test.setMaxDocumentCount(1); //setting a limit
        test.undo();
        File file = getFile(uri);
        assertTrue(file.exists());
    }
    @Test
    void testHeapDeleteAll() throws IOException {
        String a = "abc";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        String b = "abd abc";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        String c = "efg";
        InputStream ts = new ByteArrayInputStream(c.getBytes());
        URI u = URI.create("c");
        test.put(ts, u, format);
        test.search("abc");
        String d = "hij";
        InputStream t = new ByteArrayInputStream(d.getBytes());
        URI i = URI.create("d");
        test.put(t, i, format);
        test.deleteAll("abc");
        test.setMaxDocumentCount(2);
        test.undo();
        File file = getFile(u);
        File tf = getFile(i);
        assertTrue(file.exists());
        assertTrue(tf.exists());
    }
    @Test
    void testHeapDeletePre() throws IOException {
        String a = "abc";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        String b = "abd abc";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        String c = "efg";
        InputStream ts = new ByteArrayInputStream(c.getBytes());
        URI u = URI.create("c");
        test.put(ts, u, format);
        test.search("abc");
        String d = "hij";
        InputStream t = new ByteArrayInputStream(d.getBytes());
        URI i = URI.create("d");
        test.put(t, i, format);
        test.deleteAllWithPrefix("a");
        test.setMaxDocumentCount(2);
        test.undo();
        File file = getFile(u);
        File tf = getFile(i);
        assertTrue(file.exists());
        assertTrue(tf.exists());
    }
    @Test
    void testHeapOverLimitUndoURI() throws IOException {
        String a = "abc";
        InputStream isTest = new ByteArrayInputStream(a.getBytes());
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        String b = "abd abc";
        InputStream tst = new ByteArrayInputStream(b.getBytes());
        URI uri = URI.create("b");
        test.put(tst, uri, format);
        test.deleteAll("abc");
        test.setMaxDocumentCount(1);
        InputStream is = new ByteArrayInputStream(a.getBytes());
        test.put(is, x, format);
        String c = "a";
        InputStream ts = new ByteArrayInputStream(c.getBytes());
        URI u = URI.create("c");
        test.setMaxDocumentCount(2);
        test.put(ts, u, format);
        test.deleteAllWithPrefix("a");
    }
    @Test
    void testSetDocCOuntHeapOverBytes() throws IOException {
        byte[] y = new byte[2];
        y[0] = (byte) 20;
        y[1] = (byte) 25;
        InputStream isTest = new ByteArrayInputStream(y);
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.BINARY;
        URI x = URI.create("a");
        DocumentStoreImpl test = new DocumentStoreImpl();
        test.put(isTest, x, format);
        byte[] z = new byte[2];
        z[0] = (byte) 90;
        z[1] = (byte) 46;
        InputStream ts = new ByteArrayInputStream(z);
        URI uri = URI.create("b");
        test.put(ts, uri, format);
        byte[] p = new byte[2];
        p[0] = (byte) 20;
        p[1] = (byte) 25;
        InputStream is = new ByteArrayInputStream(p);
        URI b = URI.create("c");
        test.put(is, b, format);
        test.setMaxDocumentCount(2);
        File file = getFile(x);
        assertTrue(file.exists());
    }
    @Test
    void testDocByteOverByteLimit() throws IOException {
        DocumentStoreImpl x = new DocumentStoreImpl();
        DocumentStore.DocumentFormat format = DocumentStore.DocumentFormat.TXT;
        x.setMaxDocumentBytes(0);
        String d = "hij";
        InputStream t = new ByteArrayInputStream(d.getBytes());
        URI i = URI.create("d");
        x.put(t, i, format);
        File file = getFile(i);
        assertTrue(file.exists());
    }
    @Test
    void testSetDocByteLimitNegetive() throws IOException {
        DocumentStoreImpl x = new DocumentStoreImpl();
        assertThrows(IllegalArgumentException.class, () -> {
            x.setMaxDocumentBytes(-1);
        });
    }
    @Test
    void testSetDocCountLimitNegetive() throws IOException {
        DocumentStoreImpl x = new DocumentStoreImpl();
        assertThrows(IllegalArgumentException.class, () -> {
            x.setMaxDocumentCount(-1);
        });
    }
    File getFile(URI uri) {
        String domain = uri.getHost();
        String path = uri.getPath();
        String jsonFilePath;
        if (domain == null) {
            jsonFilePath = System.getProperty("user.dir") + File.separator + path + ".json";
        } else {
            jsonFilePath = System.getProperty("user.dir") + File.separator + domain + path + ".json";
        }
        File file = new File(jsonFilePath);
        return file;
    }
    @AfterEach
    public void cleanUpEach() {
        File fileA = getFile(URI.create("a"));
        if (fileA.exists()) {
            fileA.delete();
        }

        File fileB = getFile(URI.create("b"));
        if (fileB.exists()) {
            fileB.delete();
        }

        File fileC = getFile(URI.create("c"));
        if (fileC.exists()) {
            fileC.delete();
        }

        File fileD = getFile(URI.create("d"));
        if (fileD.exists()) {
            fileD.delete();
        }
    }

}