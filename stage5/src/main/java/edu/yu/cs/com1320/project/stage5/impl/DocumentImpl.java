package edu.yu.cs.com1320.project.stage5.impl;
import edu.yu.cs.com1320.project.stage5.Document;
import java.util.*;
import java.net.URI;

public class DocumentImpl implements Document{
		
	private String txt = null;
    private URI uri;
	private byte[] binaryData = null;
    private Map<String,Integer> premap;
    private HashSet<String> docwords;
    private long time;
    private Map<String,Integer> wordmap;


    public DocumentImpl(URI uri, String text, Map<String, Integer> wordCountMap){
        if (uri == null || uri.toString().length() == 0 || text == null || text.length() == 0){
            throw new IllegalArgumentException();
        }
        if(wordCountMap == null){
            this.wordmap = new HashMap<>();
            String[] txt = text.split("\\W+");
            for (String t : txt) {
                if (this.wordmap.get(t) == null) {
                    this.wordmap.put(t, 1);
                } else {
                    this.wordmap.put(t, this.wordmap.get(t) + 1);
                }
            }
        }
        else{
            setWordMap(wordCountMap);
        }
        this.uri = uri;
        this.txt = text;
        this.premap = new HashMap<>();
        this.docwords = new HashSet<>();
        mapPreTxt(text);
	}

	public DocumentImpl (URI uri, byte[] binaryData){
        if (uri == null || uri.toString().length() == 0 || binaryData == null || binaryData.length == 0){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.binaryData = binaryData;
        Map<String,Integer> temp = new HashMap<>();
        this.wordmap = temp;
	}

	/**
     * @return content of text document
     */
    public String getDocumentTxt(){
        return this.txt;
    }

    /**
     * @return content of binary data document
     */
    public byte[] getDocumentBinaryData(){
        return this.binaryData;
    }

    /**
     * @return URI which uniquely identifies this document
     */
    public URI getKey(){
        return this.uri;
    }


    private void mapPreTxt(String txt){
        String[] text = txt.split("\\W+");
        for(String t : text){
            docwords.add(t);
            for(int sub = 1; sub <= t.length(); sub++){
                String prefix = t.substring(0,sub);
                if(this.premap.get(prefix) == null){
                    this.premap.put(prefix,1);
                }
                else{
                    int num = this.premap.get(prefix);
                    this.premap.put(prefix,num + 1);
                }
            }
        }
    }

    /**
     * how many times does the given word appear in the document?
     * @param word
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */
    public int wordCount(String word) {
           if (this.binaryData != null) {
               return 0;
           }
           if(this.premap.get(word) == null){
               return 0;
           }
           return this.premap.get(word);
    }
    /**
     * @return all the words that appear in the document
     */
    public Set<String> getWords(){
            return this.docwords;
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof DocumentImpl)){
            return false;
        }
        return ((DocumentImpl) o).hashCode() == this.hashCode();
    }


    /**
     * return the last time this document was used, via put/get or via a search result
     * (for stage 4 of project)
     */
   public long getLastUseTime(){
       return time;
   }
    public void setLastUseTime(long timeInNanoseconds){
       this.time = timeInNanoseconds;
    }

    @Override
    public int compareTo(Document doc){
       if(doc == null){
           throw new NullPointerException();
       }
        if(this.getLastUseTime() < doc.getLastUseTime()){
            return -1;
        } else if (this.getLastUseTime() > doc.getLastUseTime()){
            return 1;
        }
        return 0;
    }
    /**
     * @return a copy of the word to count map so it can be serialized
     */
    public Map<String,Integer> getWordMap(){
        return this.wordmap;
    }

    /**
     * This must set the word to count map during deserialization
     * @param wordMap
     */
   public void setWordMap(Map<String,Integer> wordMap){
       this.wordmap = wordMap;
    }


    @Override
    public int hashCode(){
        int result = this.uri.hashCode();
        result = 31 * result + (this.txt != null ? this.txt.hashCode() : 0);
        result = 31 * result + (Arrays.hashCode(this.binaryData));
        return Math.abs(result);
    }


}