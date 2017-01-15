package se.sandboge.japanese.lists;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class JsonConverter {

    private String[] props;
    private int propsCount;
    private int errCount;
    private boolean hasIdProp;
    private List<Object> root = new ArrayList<>();
    private int idCount;
    private String prefix;

    void readFiles(String fileName) {
        String name;
        try (
                InputStream fis = JsonConverter.class.getResourceAsStream(fileName);
                InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                BufferedReader br = new BufferedReader(isr)
        ) {
            while ((name = br.readLine()) != null) {
                String primarySeparator = br.readLine();
                String secondarySeparator = br.readLine();
                if (primarySeparator == null || secondarySeparator == null) {
                    System.out.println("Malformed file specification!");
                    break;
                }
                readFile(name, primarySeparator, secondarySeparator);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void readFile(String fileName, String primarySeparator, String secondarySeparator) {
        String line;
        boolean firstLine = true;
        prefix = fileName.substring(1, fileName.indexOf('.'));
        try (
                InputStream fis = JsonConverter.class.getResourceAsStream(fileName);
                InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                BufferedReader br = new BufferedReader(isr)
        ) {
            while ((line = br.readLine()) != null) {
                Map<String, Object> nugget = new HashMap<>();
                if (firstLine) {
                    firstLine = false;
                    processFirstLine(line, primarySeparator);
                    continue;
                }
                processLine(line, primarySeparator, secondarySeparator, nugget);
                if (errCount > 10) {
                    System.out.println("Too many errors, aborting processing of " + fileName);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writeValueAsString(root));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void processFirstLine(String line, String separator) {
        props = line.split(Pattern.quote(separator));
        propsCount = props.length;
        errCount = 0;
        hasIdProp = false;
        System.out.println(line + " : " + props.length);
        for (String prop : props) {
            if ("id".equals(prop)) {
                hasIdProp = true;
                break;
            }
        }
    }

    private void processLine(String line, String primary, String secondary, Map<String, Object> nugget) {
        String[] items = line.split(Pattern.quote(primary));
        if (items.length != propsCount && !(items.length == propsCount - 1 && line.endsWith(primary))) {
            errCount++;
            System.out.println("Error on line: " + line + " : " + items.length);
            return;
        }
        for (int i = 0; i < items.length; i++) {
            switch (props[i]) {
                case "id":
                    nugget.put("id", generateId(items[i]));
                    break;
                case "state":
                case "jnlp":
                    nugget.put(props[i], items[i]);
                    break;
                default:
                    if (!items[i].equals("")) {
                        ArrayList<String> value = handleFact(items[i], secondary);
                        nugget.put(props[i], value);
                    }
                    break;
            }
        }
        if (!hasIdProp) {
            nugget.put("id", generateId(""));
        }
        root.add(nugget);
        System.out.println(line + " : " + items.length);
    }

    private ArrayList<String> handleFact(String value, String separator) {
        String[] values = value.split(Pattern.quote(separator));
        return new ArrayList<>(Arrays.asList(values));

    }

    private String generateId(String id) {
        idCount ++;
        if (id.equals("")) {
            return prefix + idCount;
        } else {
            return id;
        }
    }
}
