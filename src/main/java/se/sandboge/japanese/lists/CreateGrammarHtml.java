package se.sandboge.japanese.lists;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;

public class CreateGrammarHtml {

    private String[] props;
    private int propsCount;
    private String body = "";
    private String index = "";

    public static void main(String[] args) throws IOException {
        CreateGrammarHtml jc = new CreateGrammarHtml();
        jc.readFile();
    }

    private void readFile() {
        String line;
        boolean firstLine = true;
        try (
                InputStream fis = CreateGrammarHtml.class.getResourceAsStream("/gcon.csv");
                InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                BufferedReader br = new BufferedReader(isr)
        ) {
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    processFirstLine(line);
                } else {
                    processLine(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("=============");
        System.out.println(body);
        System.out.println("=============");
        System.out.println(index);
        System.out.println("=============");
    }

    private void processFirstLine(String line) {
        props = line.split(Pattern.quote("|"));
        propsCount = props.length;
        System.out.println(line + " > " + props.length);
    }

    private void processLine(String line) {
        String[] items = line.split(Pattern.quote("|"));
        if (items.length != propsCount) {
            System.out.println("Error on line: " + line + " : " + items.length);
        }
        String jpShort = "";
        String seShort = "";
        String seLong = "";
        String genki = "";
        String id = "";
        for (int i = 0; i < items.length; i++) {
            switch (props[i]) {
                case "id":
                    id = (items[i]);
                    break;
                case "jp_short":
                    jpShort = items[i];
                    break;
                case "se_short":
                    seShort = items[i];
                    break;
                case "se_long":
                    seLong = items[i];
                    break;
                case "genki":
                    genki = items[i];
                    break;
            }
        }
        System.out.println("Genki " + genki +" item: " + seShort);
        body += "<a id=\"" + id + "\"></a><h2>" + jpShort + " - " + seShort + "</h2>\n";
        body += "<p>" + seLong + "</p><p>Textbok kap. " + genki + "</p><hr>\n";
        index += "<li><a href=\"g1200a.html#" + id + "\">" + jpShort + " - " + seShort + "</a></li>\n";
    }
}
