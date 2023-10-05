package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DocumentPersistenceManagerTest {
    private URI uri = URI.create("https://example.com/path/to/resource");
    @Test
    void testSerielizetxt() throws IOException {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        DocumentImpl x = new DocumentImpl(this.uri, "Any Avi As Always ami 123", null);
        x.setLastUseTime(System.nanoTime());
        dpm.serialize(this.uri, x);
        Document temp = dpm.deserialize(this.uri);
        assertEquals(null, temp.getDocumentBinaryData());
        assertEquals(0, temp.getLastUseTime());
    }
    @Test
    void testSerielizeByte() throws IOException {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        byte[] x = new byte[2];
        x[0] = (byte) 3;
        x[1] = (byte) 7;
        DocumentImpl test = new DocumentImpl(this.uri, x);
        byte[] bytes = test.getDocumentBinaryData();
        test.setLastUseTime(System.nanoTime());
        dpm.serialize(this.uri, test);
        Document temp = dpm.deserialize(this.uri);
        assertEquals(Arrays.toString(bytes), Arrays.toString(temp.getDocumentBinaryData()));
        assertEquals(null, temp.getDocumentTxt());
        assertEquals(0, temp.getLastUseTime());
    }
    @Test
    void testDesktopSerielizetxt() throws IOException {
        String desktopPath = System.getProperty("user.home") + "/Desktop";
        File baseDir = new File(desktopPath);
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(baseDir);
        DocumentImpl x = new DocumentImpl(this.uri, "Any Avi As Always ami 123", null);
        x.setLastUseTime(System.nanoTime());
        System.out.println(x.getLastUseTime());
        dpm.serialize(this.uri, x);
        dpm.delete(this.uri);
    }

}