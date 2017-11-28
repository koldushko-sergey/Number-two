package dictionary;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Dictionary {
    private final static char SEPARATOR = '=';
    private Map<String, String> dictionary = new HashMap<>();

    private Logger log = Logger.getLogger(Dictionary.class.getName());

    public Dictionary() {
    }


    public void load(String path) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            while (reader.ready()){
                String line = reader.readLine();
                convertAndAddWordInDictionary(line);
            }
        }
        catch (IOException ex){
            log.error("File path " + path + " not find.");
            throw new IOException("File path " + path + " not find.", ex);
        }
    }

    public String get(String key){
        return dictionary.get(key);
    }

    public Set<String> keySet(){
        return dictionary.keySet();
    }


    private void convertAndAddWordInDictionary(String line){
        String key = line.substring(0, line.indexOf(SEPARATOR));
        String value = line.substring(line.indexOf(SEPARATOR) + 1, line.length());
        dictionary.put(key, value);
    }

}
