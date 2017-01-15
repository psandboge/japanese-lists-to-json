package se.sandboge.japanese.lists;

import org.junit.Test;

import static org.junit.Assert.*;

public class JsonConverterTest {
    @Test
    public void readFile() throws Exception {
        JsonConverter j = new JsonConverter();

        j.readFile("/TestFile1.txt",",",";");
    }

}