package edu.yu.cs.com1320.project.stage5.impl;
import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore {
    private BTreeImpl < URI, Document > bTree;
    private StackImpl < Undoable > stack;
    private TrieImpl < URI > trie;
    private MinHeapImpl < Node > minheap;
    private DocumentPersistenceManager dm;
    private HashMap < URI, Node > uriToNode = new HashMap < > ();
    private HashMap < Node, URI > nodeToUri = new HashMap < > ();
    private HashSet < URI > onDisk = new HashSet < > ();
    private int doclimit = -1; //ability to choose a document # limit
    private int bytelimit = -1; //ability to choose a document byte limit
    private int doccount = 0; //number of documents added
    private int bytecount = 0; //number of bytes added

    private class Node implements Comparable < Node > {
        URI uri;
        private Node(URI uri) {
            this.uri = uri;
        }
        private URI getUri() {
            return this.uri;
        }

        @Override
        public int compareTo(Node uri) {
            if (uri == null) {
                throw new NullPointerException();
            }
            if (getDoc(this.getUri()).getLastUseTime() < getDoc(uri.getUri()).getLastUseTime()) {
                return -1;
            } else if (getDoc(this.getUri()).getLastUseTime() > getDoc(uri.getUri()).getLastUseTime()) {
                return 1;
            }
            return 0;
        }
    }

    public DocumentStoreImpl() {
        this.bTree = new BTreeImpl < URI, Document > ();
        this.dm = new DocumentPersistenceManager(null);
        this.bTree.setPersistenceManager(dm);
        this.stack = new StackImpl < Undoable > ();
        this.trie = new TrieImpl < URI > ();
        this.minheap = new MinHeapImpl < Node > ();
    }
    public DocumentStoreImpl(File baseDir) {
        this.bTree = new BTreeImpl < URI, Document > ();
        this.dm = new DocumentPersistenceManager(baseDir);
        this.bTree.setPersistenceManager(dm);
        this.stack = new StackImpl < Undoable > ();
        this.trie = new TrieImpl < URI > ();
        this.minheap = new MinHeapImpl < Node > ();
    }

  /*
    the two document formats supported by this document store.
    Note that TXT means plain text, i.e. a String.
    */

    /**
     * @param input the document being put
     * @param uri unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     * @throws IOException if there is an issue reading input
     * @throws IllegalArgumentException if uri or format are null
     */
    public int put(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if (uri == null || format == null) {
            throw new IllegalArgumentException();
        }
        //this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
        if (input == null) {
            Document get = get(uri);
            if (get != null) {
                int val = get.hashCode();
                delete(uri);
                return val;
            }
            return 0;
        }

        DocumentImpl doc = null;
        byte[] byteArray;
        try {
            byteArray = input.readAllBytes();
        } catch (IOException e) {
            throw new IOException();
        }
        if (format == DocumentFormat.BINARY) { //if it is a binary document
            doc = new DocumentImpl(uri, byteArray);
        } else if (format == DocumentFormat.TXT) { //if it is a text document
            String txt = new String(byteArray);
            doc = new DocumentImpl(uri, txt, null);
        }
        //first time putting it into the hashtable
        if (get(uri) == null) {
            doc.setLastUseTime(System.nanoTime());
            Node node = new Node(uri); //create new node entry for this document
            this.nodeToUri.put(node, uri); //update the node to uri map
            this.uriToNode.put(uri, node); //update the uri to node map
            Node nodetem = this.uriToNode.get(uri); ///holding the temp for undo's
            this.bTree.put(uri, doc); //add to btree
            //put it in the minheap
            putMinHeap(node); // this adds to heap and maintains document and byte count -- we set the time above already
            //put it in the hashtable
            Function < URI, Boolean > function; // function created for the undo
            DocumentImpl docimp = doc;
            // checking to see if it is binary or text - if text than add to trie
            if (doc.getDocumentBinaryData() == null) {
                Set < String > words = doc.getWords();
                for (String w: words) {
                    this.trie.put(w, uri);
                }

                function = (URI u) -> { //setting our function
                    --this.doccount; //maintain document and byte count
                    this.bytecount -= getdocbytes(docimp);
                    docimp.setLastUseTime(0);
                    this.minheap.reHeapify(node); //send doc to top of heap
                    this.minheap.remove(); //delete from heap
                    this.bTree.put(u, null); //delete from btree
                    for (String del: words) {
                        this.trie.delete(del, uri); //delete from trie
                    }
                    if (this.onDisk.contains(uri)) {
                        this.onDisk.remove(uri);
                    }
                    this.uriToNode.remove(uri); //removing from the uri to node map
                    this.nodeToUri.remove(node); //removing from the node to uri map
                    return true;
                };
                GenericCommand < URI > gc = new GenericCommand < > (uri, function);
                this.stack.push(gc);
                return 0;
            }
            //must be binary data - don't add to trie
            else {
                function = (URI u) -> {
                    --this.doccount; //maintain document and byte count
                    this.bytecount -= getdocbytes(docimp);
                    docimp.setLastUseTime(0);
                    this.minheap.reHeapify(node); //send doc to top of heap
                    this.minheap.remove(); //delete from heap
                    this.bTree.put(u, null); // delete from btree
                    if (this.onDisk.contains(uri)) {
                        this.onDisk.remove(uri);
                    }
                    this.uriToNode.remove(uri); //removing from the uri to node map
                    this.nodeToUri.remove(node); //removing from the node to uri map
                    return true;
                };
                GenericCommand < URI > gc = new GenericCommand < > (uri, function);
                this.stack.push(gc);
                return 0;
            }
        }
        //updating the current value in the tree
        else {
            doc.setLastUseTime(System.nanoTime());
            Node node = this.uriToNode.get(uri); //create new node entry for this document
            //Node nodetem = node; ///holding the temp for undo's
            int dochash = get(uri).hashCode(); //the hashcode to return
            DocumentImpl doctem = (DocumentImpl) get(uri); //used for undo, will also update the on disk
            doctem.setLastUseTime(0);
            this.minheap.reHeapify(node); //send old doc to top of heap making it ready for delete
            //this.minheap.remove(); //delete old document
            this.bytecount -= getdocbytes(doctem); //update the byte count (with the old document removed)
            this.bTree.put(uri, doc); //updating btree
            DocumentImpl presentdoc = doc; //used for undo
            this.bytecount += getdocbytes(presentdoc); //update the byte count
            Function < URI, Boolean > function; //function for the undo
            //check if it is a text or binary - if text add to the trie
            if (doc.getDocumentBinaryData() == null) { //it is a txt doc
                Set < String > wd = doctem.getWords();
                for (String q: wd) {
                    this.trie.delete(q, uri); //deleting old document from trie
                }
                Set < String > words = doc.getWords();
                for (String w: words) {
                    this.trie.put(w, uri); //adding new document to trie
                }

                function = (URI u) -> { //function for the undo
                    for (String del: words) {
                        this.trie.delete(del, uri); //deleting present doc from trie
                    }
                    for (String reput: wd) {
                        this.trie.put(reput, uri); //adding old doc back into trie
                    }
                    this.bTree.put(uri, doctem); //putting old doc back into hashtable
                    doctem.setLastUseTime(System.nanoTime()); //setting time for old document being put into h
                    this.minheap.reHeapify(node); //send present doc to top of heap making it ready for delete
                    this.bytecount -= getdocbytes(presentdoc); //update the byte count (with the present document removed)
                    this.bytecount += getdocbytes(doctem);
                    if (this.onDisk.contains(uri)) {
                        this.onDisk.remove(uri);
                    }
                    return true;
                };
                GenericCommand < URI > gc = new GenericCommand < > (uri, function);
                this.stack.push(gc);
                return dochash;
            }
            //must be binary - do not add to trie
            else {
                function = (URI u) -> {
                    this.bTree.put(uri, doctem); //putting old doc back into hashtable
                    doctem.setLastUseTime(System.nanoTime()); //setting time for old document being put into h
                    this.minheap.reHeapify(node); //send present doc to top of heap making it ready for delete
                    this.bytecount -= getdocbytes(presentdoc); //update the byte count (with the present document removed)
                    this.bytecount += getdocbytes(doctem);
                    if (this.onDisk.contains(uri)) {
                        this.onDisk.remove(uri);
                    }
                    return true;
                };
                GenericCommand < URI > gc = new GenericCommand < > (uri, function);
                this.stack.push(gc);
                return dochash;
            }
        }

    }

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    public Document get(URI uri) {
        if (this.onDisk.contains(uri)) {
            putMinHeap((this.uriToNode.get(uri))); //check if I need to update the heap and update if necessary
            Document temp = this.bTree.get(uri);
            temp.setLastUseTime(System.nanoTime()); //update the time accessed
            this.minheap.reHeapify(this.uriToNode.get(uri)); //update the heap
            this.onDisk.remove(uri);
            return temp;
        } else { //regular get
            if (this.bTree.get(uri) == null) {
                return null;
            }
            Document temp = this.bTree.get(uri);
            temp.setLastUseTime(System.nanoTime()); //update the time accessed
            this.minheap.reHeapify(this.uriToNode.get(uri)); //update the heap
            return temp;
        }
    }
    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    public boolean delete(URI uri) {
        if (get(uri) == null) {
            return false;
        } else {
            DocumentImpl t = (DocumentImpl) get(uri); //will remove from on disk
            Node temp = this.uriToNode.get(uri);
            Function < URI, Boolean > function;
            t.setLastUseTime(0);
            this.minheap.reHeapify(temp); //send delete doc to top of heap making it ready for delete
            this.minheap.remove(); //delete document from heap
            this.bytecount -= getdocbytes(t); //update the byte count (with the document removed)
            --doccount; //update the document count (with the document removed)
            //check to see if deleting txt doc - delete from the trie
            if (t.getDocumentBinaryData() == null) { //deleting txt
                Set < String > words = t.getWords();
                for (String y: words) {
                    this.trie.delete(y, uri); // deleting document from trie table
                }

                function = (URI u) -> {
                    for (String w: words) {
                        this.trie.put(w, uri); //add back to trie
                    }
                    this.bTree.put(u, t); //add back to btree
                    t.setLastUseTime(System.nanoTime());
                    this.uriToNode.put(uri, temp);
                    this.nodeToUri.put(temp, uri);
                    putMinHeap(temp); // this adds to heap and maintains document and byte count
                    return true;
                };
                GenericCommand < URI > gc = new GenericCommand < > (uri, function);
                this.stack.push(gc);
            }
            // it is binary - no need to delete from trie
            else {
                function = (URI u) -> {
                    this.bTree.put(u, t); //add back to hashtable
                    t.setLastUseTime(System.nanoTime());
                    this.uriToNode.put(uri, temp);
                    this.nodeToUri.put(temp, uri);
                    putMinHeap(temp); // this adds to heap and maintains document and byte count
                    return true;
                };
                GenericCommand < URI > gc = new GenericCommand < > (uri, function);
                this.stack.push(gc);
            }
            this.bTree.put(uri, null); // deleting document from hashtable
            this.uriToNode.remove(uri);
            this.nodeToUri.remove(temp);
            if (this.onDisk.contains(uri)) {
                this.onDisk.remove(uri);
            }
            return true;
        }
    }

    /**
     * undo the last put or delete command
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */

    public void undo() throws IllegalStateException {
        if (this.stack.size() == 0) {
            throw new IllegalStateException();
        }
        if (this.stack.peek() instanceof CommandSet < ? > ) {
            CommandSet < URI > commands = (CommandSet < URI > ) this.stack.pop();
            commands.undoAll();
        } else {
            GenericCommand < URI > reverse = (GenericCommand < URI > ) this.stack.pop();
            reverse.undo();
        }
    }

    /**
     * undo the last put or delete that was done with the given URI as its key
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    public void undo(URI uri) throws IllegalStateException {
        GenericCommand < URI > rev = null;
        CommandSet < URI > undo = null;
        StackImpl < Undoable > temp = new StackImpl < Undoable > ();
        while (rev == null && undo == null && this.stack.size() != 0) {
            if (this.stack.peek() instanceof CommandSet < ? > ) {
                CommandSet < URI > commands = (CommandSet < URI > ) this.stack.pop();
                if (commands.containsTarget(uri) == true) {
                    undo = commands;
                }
                temp.push(commands);
            } else {
                GenericCommand < URI > ex = (GenericCommand < URI > ) this.stack.pop();
                if (uri.equals(ex.getTarget())) {
                    rev = ex;
                }
                temp.push(ex);
            }
        }
        if (rev == null && undo == null) {
            throw new IllegalStateException();
        }
        if (rev == null) {
            if (undo.size() == 1) {
                temp.pop();
                while (temp.size() != 0) {
                    this.stack.push(temp.pop());
                }
                undo.undoAll();
            } else {
                temp.pop();
                undo.undo(uri);
                this.stack.push(undo);
                while (temp.size() != 0) {
                    this.stack.push(temp.pop());
                }
            }
        } else {
            temp.pop();
            while (temp.size() != 0) {
                this.stack.push(temp.pop());
            }
            rev.undo();
        }

    }
    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE SENSITIVE.
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List < Document > search(String keyword) {
        Comparator < URI > comparator = new Comparator < > () {
            @Override
            public int compare(URI q, URI r) {
                return get(r).wordCount(keyword) - get(q).wordCount(keyword);
            }
        };
        List < URI > uris = this.trie.getAllSorted(keyword, comparator);
        long updatetime = System.nanoTime(); // updated time for documents
        List < Document > documents = new ArrayList < > ();
        for (URI uri: uris) {
            Document temp = get(uri);
            temp.setLastUseTime(updatetime); //update time of documents
            this.minheap.reHeapify(this.uriToNode.get(uri)); //update the heap
            documents.add(temp);
        }
        return documents;
    }

    /**
     * Retrieve all documents whose text starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE SENSITIVE.
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List < Document > searchByPrefix(String keywordPrefix) {
        Comparator < URI > comparator = new Comparator < > () {
            @Override
            public int compare(URI q, URI r) {
                return get(r).wordCount(keywordPrefix) - get(q).wordCount(keywordPrefix);
            }
        };
        List < URI > uris = this.trie.getAllWithPrefixSorted(keywordPrefix, comparator);
        long updatetime = System.nanoTime(); // updated time for documents
        List < Document > documents = new ArrayList < > ();
        for (URI uri: uris) {
            Document temp = get(uri);
            temp.setLastUseTime(updatetime); //update time of documents
            this.minheap.reHeapify(this.uriToNode.get(uri)); //update the heap
            documents.add(temp);
        }
        return documents;
    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * Search is CASE SENSITIVE.
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    public Set < URI > deleteAll(String keyword) {
        Set < URI > uris = this.trie.deleteAll(keyword); //remove documents from trie
        for (URI u: uris) {
            Set < String > wd = this.bTree.get(u).getWords();
            for (String q: wd) {
                this.trie.delete(q, u); //deleting old document from trie
            }
        }
        //create a commandset and add it to the stack
        CommandSet < URI > commands = new CommandSet < > ();
        for (URI uri: uris) {
            Document doctemp = get(uri);
            Node node = this.uriToNode.get(uri);
            Function < URI, Boolean > function = (URI u) -> {
                for (String txt: doctemp.getWords()) {
                    this.trie.put(txt, uri); //putting documents back into trie
                }
                doctemp.setLastUseTime(System.nanoTime()); //update time for heap
                this.bTree.put(uri, doctemp); //putting documents back into hashtable
                this.uriToNode.put(uri, node);
                this.nodeToUri.put(node, uri);
                putMinHeap(node); // put documents back into heap and maintain counts
                return true;
            };
            GenericCommand < URI > gc = new GenericCommand < > (uri, function);
            commands.addCommand(gc);
        }
        this.stack.push(commands);
        for (URI uri: uris) {
            Document heapdoc = get(uri);
            --doccount; //update document count
            bytecount -= getdocbytes(heapdoc); //update byte count
            heapdoc.setLastUseTime(0);
            this.minheap.reHeapify(this.uriToNode.get(uri));
            this.minheap.remove(); // remove documents from heap
            this.bTree.put(uri, null);
            this.nodeToUri.remove(this.uriToNode.get(uri));
            this.uriToNode.remove(uri);
            if (this.onDisk.contains(uri)) {
                this.onDisk.remove(uri);
            }
        }
        return uris;
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE SENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    public Set < URI > deleteAllWithPrefix(String keywordPrefix) {
        Set < URI > uris = this.trie.deleteAllWithPrefix(keywordPrefix); //remove documents from trie
        for (URI u: uris) {
            Set < String > wd = this.bTree.get(u).getWords();
            for (String q: wd) {
                this.trie.delete(q, u); //deleting old document from trie
            }
        }
        //create a commandset and add it to the stack
        CommandSet < URI > commands = new CommandSet < > ();
        for (URI uri: uris) {
            Document doctemp = get(uri);
            Node node = this.uriToNode.get(uri);
            Function < URI, Boolean > function = (URI u) -> {
                for (String txt: doctemp.getWords()) {
                    this.trie.put(txt, uri); //putting documents back into trie
                }
                doctemp.setLastUseTime(System.nanoTime()); //update time for heap
                this.bTree.put(uri, doctemp); //putting documents back into hashtable
                this.uriToNode.put(uri, node);
                this.nodeToUri.put(node, uri);
                putMinHeap(node); // put documents back into heap and maintain counts
                return true;
            };
            GenericCommand < URI > gc = new GenericCommand < > (uri, function);
            commands.addCommand(gc);
        }
        this.stack.push(commands);
        for (URI uri: uris) {
            Document heapdoc = get(uri);
            --doccount; //update document count
            bytecount -= getdocbytes(heapdoc); //update byte count
            heapdoc.setLastUseTime(0);
            this.minheap.reHeapify(this.uriToNode.get(uri));
            this.minheap.remove(); // remove documents from heap
            this.bTree.put(uri, null);
            this.nodeToUri.remove(this.uriToNode.get(uri));
            this.uriToNode.remove(uri);
            if (this.onDisk.contains(uri)) {
                this.onDisk.remove(uri);
            }
        }
        return uris;
    }
    /**
     * set maximum number of documents that may be stored
     * @param limit
     */
    public void setMaxDocumentCount(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException();
        }
        while (limit < this.doccount) {
            deleteWholeDoc(this.minheap.remove());
        }
        this.doclimit = limit;
    }

    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     * @param limit
     */
    public void setMaxDocumentBytes(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException();
        }
        while (limit < this.bytecount) {
            deleteWholeDoc(this.minheap.remove());
        }
        this.bytelimit = limit;
    }

    /**
     * insert document into heap. If it is over the limit we must first clear up space then insert.
     * @param node
     */
    private void putMinHeap(Node node) {
        //no limits have been set, add doc and update the counts
        if (this.bytelimit == -1 & this.doclimit == -1) {
            this.minheap.insert(node);
            ++doccount;
            bytecount += getdocbytes(this.bTree.get(this.nodeToUri.get(node)));
            return;
        }
        if ((this.bytelimit > -1 && getdocbytes(this.bTree.get(this.nodeToUri.get(node))) > this.bytelimit) || this.doclimit == 0) {
            moveToDisk(node);
            return;
        }
        //if it only has a document # limit
        if (this.doclimit > -1 && this.bytelimit == -1) {
            if (this.doclimit == this.doccount) {
                deleteWholeDoc(this.minheap.remove());
            }
        }
        //if it only has a byte limit
        if (this.doclimit == -1 && this.bytelimit > -1) {
            while (this.bytelimit < (this.bytecount + getdocbytes(this.bTree.get(this.nodeToUri.get(node))))) {
                deleteWholeDoc(this.minheap.remove());
            }
        }
        //if it has both limits
        if (this.doclimit > -1 && this.bytelimit > -1) {
            while (this.bytelimit < (this.bytecount + getdocbytes(this.bTree.get(this.nodeToUri.get(node)))) || this.doclimit == this.doccount) {
                deleteWholeDoc(this.minheap.remove());
            }
        }
        this.minheap.insert(node);
        ++doccount;
        bytecount += getdocbytes(this.bTree.get(this.nodeToUri.get(node)));
    }

    /**
     * return the total number of bytes of the document(i.e. = the length of bite)
     * @param doc
     * @return int
     */
    private int getdocbytes(Document doc) {
        if (doc.getDocumentBinaryData() == null) {
            return (doc.getDocumentTxt().getBytes()).length;
        }
        return (doc.getDocumentBinaryData().length);
    }
    /**
     * delete all vestiges of the document in memory and update the counts
     * @param node
     */
    private void deleteWholeDoc(Node node) {
        URI uri = this.nodeToUri.get(node);
        --doccount;
        bytecount -= getdocbytes(this.bTree.get(uri));
        try {
            this.bTree.moveToDisk(uri); //move it to disk on btree
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.onDisk.add(uri); //add the uri to set of the uri's on disk
    }
    private void moveToDisk(Node node) {
        URI uri = this.nodeToUri.get(node);
        try {
            this.bTree.moveToDisk(uri); //move it to disk on btree
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.onDisk.add(uri); //add the uri to set of the uri's on disk
    }

    private Document getDoc(URI uri) {
        return this.bTree.get(uri);
    }
}