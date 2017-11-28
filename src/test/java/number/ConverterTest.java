package number;

import dictionary.Dictionary;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ConverterTest {
    Converter converter;
    @Before
    public void init(){
        converter = new Converter();
    }
    @Test
    public void toWord_test() throws IOException{
        Dictionary dictionary = new Dictionary();
        dictionary.load("src/main/resources/test.txt");
        for (String key : dictionary.keySet()) {
            if (!converter.toWords(key).equals(dictionary.get(key))){
                throw new AssertionError("The comparison give the wrong result. Number " + key +
                        ", word " + converter.toWords(key));
            }
        }
    }
}
