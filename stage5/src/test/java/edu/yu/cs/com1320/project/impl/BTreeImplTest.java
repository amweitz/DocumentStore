package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class BTreeImplTest {
    @Test
    void TestBtreePut() {
        BTreeImpl<Integer,String> number = new BTreeImpl<Integer, String>();
        number.put(1,"Avi");
        String x = number.put(1,"Weitz");
        assertEquals("Avi", x,"x should be Avi");
    }
    @Test
    void TestBtreeGet() {
        BTreeImpl<Integer,String> number = new BTreeImpl<Integer, String>();
        number.put(1,"Avi");
        String x = number.get(1);
        assertEquals("Avi", x,"x should be Avi");
    }
    @Test
    void TestBtreeDelete() {
        BTreeImpl<Integer,String> number = new BTreeImpl<Integer, String>();
        number.put(1,"Avi");
        number.put(1,null);
        String x = number.get(1);
        assertEquals(null, x,"x should be Avi");
    }
    @Test
    void TestBtreeMovetodisk() throws Exception {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        URI uri = URI.create("https://example.com/path/to/resource");
        DocumentImpl x = new DocumentImpl(uri, "Any Avi As Always ami 123", null);
        BTreeImpl<URI, Document> number = new BTreeImpl<URI, Document>();
        number.setPersistenceManager(dpm);
        number.put(uri,x);
        int y = number.get(uri).hashCode();
        number.moveToDisk(uri);
        int z = number.get(uri).hashCode();
        assertEquals(y,z);

    }
    @Test
    void TestBtreeUpdatingDocOndisk() throws Exception {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        URI uri = URI.create("https://example.com/path/to/resource");
        DocumentImpl x = new DocumentImpl(uri, "Any Avi As Always ami 123", null);
        BTreeImpl<URI, Document> number = new BTreeImpl<URI, Document>();
        number.setPersistenceManager(dpm);
        number.put(uri,x);
        number.moveToDisk(uri);
        DocumentImpl y = new DocumentImpl(uri, "lets go", null);
        String domain = uri.getHost();
        String path = uri.getPath();
        String jsonFilePath;
        if(domain == null){
            jsonFilePath =  System.getProperty("user.dir") + File.separator + path + ".json";
        }
        else{
            jsonFilePath =  System.getProperty("user.dir") + File.separator + domain + path + ".json";
        }
        File file = new File(jsonFilePath);
        assertTrue(file.exists());
        number.put(uri,y);
        assertFalse(file.exists());
    }
    @Test
    void TestBtreeNullPutDocOndisk() throws Exception {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        URI uri = URI.create("https://example.com/path/to/resource");
        DocumentImpl x = new DocumentImpl(uri, "Any Avi As Always ami 123", null);
        BTreeImpl<URI, Document> number = new BTreeImpl<URI, Document>();
        number.setPersistenceManager(dpm);
        number.put(uri,x);
        number.moveToDisk(uri);
        String domain = uri.getHost();
        String path = uri.getPath();
        String jsonFilePath;
        if(domain == null){
            jsonFilePath =  System.getProperty("user.dir") + File.separator + path + ".json";
        }
        else{
            jsonFilePath =  System.getProperty("user.dir") + File.separator + domain + path + ".json";
        }
        File file = new File(jsonFilePath);
        assertTrue(file.exists());
        number.put(uri,null);
        assertFalse(file.exists());
    }
}