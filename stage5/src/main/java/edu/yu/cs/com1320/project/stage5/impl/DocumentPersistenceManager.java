package edu.yu.cs.com1320.project.stage5.impl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import com.google.gson.*;
import jakarta.xml.bind.DatatypeConverter;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
//import java.util.Base64;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {
    private File rootdir;
     class Serializer implements JsonSerializer<Document> {
         @Override
         public JsonElement serialize(Document document, Type type, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("uri", (document.getKey().toString()));
            if(document.getDocumentTxt() == null){
                byte[] binaryData = document.getDocumentBinaryData();
                String data = DatatypeConverter.printBase64Binary(binaryData);
                //String data = Base64.getEncoder().encodeToString(binaryData);
                jsonObject.addProperty("binary", data);
            }
            else{
                jsonObject.addProperty("txt", document.getDocumentTxt());
                JsonObject mapobject = new JsonObject();
                Map<String, Integer> wordMap = document.getWordMap();
                for (String entry : wordMap.keySet()) {
                    mapobject.addProperty(entry, wordMap.get(entry));
                }
                jsonObject.add("docmap", mapobject);
            }
            return jsonObject;
        }
    }
     class Deserializer implements JsonDeserializer<Document> {
         @Override
         public Document deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
             JsonObject jsonObject = json.getAsJsonObject();
             URI uri = URI.create(jsonObject.get("uri").getAsString());
             String txt = null;
             byte[] binaryData = null;
             DocumentImpl temp;
             if(jsonObject.get("txt") == null){
                 //binaryData = Base64.getDecoder().decode(jsonObject.get("binary").getAsString());
                 binaryData = DatatypeConverter.parseBase64Binary(jsonObject.get("binary").getAsString());
                 temp = new DocumentImpl(uri, binaryData);
             }
             else{
                txt = jsonObject.get("txt").getAsString();
                 JsonObject mapObject = jsonObject.get("docmap").getAsJsonObject();
                 Map<String, Integer> map = new HashMap<>();
                 for (Map.Entry<String, JsonElement> entry : mapObject.entrySet()) {
                     String key = entry.getKey();
                     Integer value = entry.getValue().getAsInt();
                     map.put(key, value);
                 }
                 temp = new DocumentImpl(uri,txt,map);
             }
             return temp;
         }
     }
    public DocumentPersistenceManager(File baseDir){
        if(baseDir == null){
            this.rootdir = new File(System.getProperty("user.dir"));
        }
        else{
            this.rootdir = baseDir;
        }
    }


    @Override
    public void serialize(URI uri, Document val) throws IOException {
        Serializer serializer = new Serializer();
        JsonElement jsonElement = serializer.serialize(val, Document.class, null);
        String json = jsonElement.toString();
        addFile(json,uri); //call the addfile to creat and store a new file
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
         Deserializer deserializer = new Deserializer();
        JsonElement json = getFile(uri);
        Document doc = deserializer.deserialize(json,Document.class, null);
        return doc;
    }

    @Override
    public boolean delete(URI uri) throws IOException { //need to test and make sure this is good
        String domain = uri.getHost();
        String path = uri.getPath();
        String jsonFilePath;
        if(domain == null){
            jsonFilePath =  this.rootdir + File.separator + path + ".json";
        }
        else{
            jsonFilePath =  this.rootdir + File.separator + domain + path + ".json";
        }
        File fileToDelete = new File(jsonFilePath);
        if (!fileToDelete.exists()) {
            return false; // File doesn't exist, so can't delete it
        }
        if (!fileToDelete.isFile()) {
            return false; // Not a file, so can't delete it
        }
        if (!fileToDelete.canWrite()) {
            return false; // No write permission, so can't delete it
        }
        Files.delete(fileToDelete.toPath());
            return true; // Deletion was successful
    }

    private void addFile(String json, URI uri) throws IOException {
        URI u = URI.create(uri + ".json");
        String domain = u.getHost();  // Extract the domain name from the URI
        String dirPath = u.getPath().substring(0, u.getPath().lastIndexOf('/') + 1); // Extract the directory path from the URI
        File dir; // Create the directory if it doesn't exist
        if(domain == null){
            dir = new File(this.rootdir + "/" + dirPath);
        }
        else{
            dir = new File(this.rootdir + "/" + domain + dirPath);
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // Write the JSON string to the new file
            String fileName = u.getPath().substring(u.getPath().lastIndexOf('/') + 1);
            String filePath = dir.getAbsolutePath() + "/" + fileName;
            FileWriter writer = new FileWriter(filePath);
            writer.write(json);
            writer.close();
    }
    private JsonElement getFile(URI uri) throws IOException {
        String domain = uri.getHost();
        String path = uri.getPath();
        String jsonFilePath;
        if(domain == null){
            jsonFilePath =  this.rootdir + File.separator + path + ".json";
        }
        else{
            jsonFilePath =  this.rootdir + File.separator + domain + path + ".json";
        }
        String j = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        Gson gson = new Gson();
        return gson.fromJson(j, JsonElement.class);
    }
}
