package se.sandboge.japanese.lists;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.sandboge.japanese.conjugation.Verb;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;

public class CreateJsonGrammarQuiz {

    private String[] props;
    private int propsCount;
    private int errCount;
    private List<Object> root = new ArrayList<>();
    private int idCount;
    private String prefix;

    public static void main(String[] args) throws IOException {
        CreateJsonGrammarQuiz jc = new CreateJsonGrammarQuiz();
        jc.readFiles("te-form");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("grammarquiz.json"), jc.root);
    }

    private void readFiles(String quizName) {
        String name;
        try (
                InputStream fis = CreateJsonGrammarQuiz.class.getResourceAsStream("/files.txt");
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
                readFile(name, primarySeparator, secondarySeparator, quizName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile(String fileName, String primarySeparator, String secondarySeparator, String quizName) {
        String line;
        boolean firstLine = true;
        prefix = "gq_" + fileName.substring(1, fileName.indexOf('.'));
        try (
                InputStream fis = CreateJsonGrammarQuiz.class.getResourceAsStream(fileName);
                InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                BufferedReader br = new BufferedReader(isr)
        ) {
            boolean writeError = true;
            while ((line = br.readLine()) != null) {
                Map<String, Object> nugget = new HashMap<>();
                if (firstLine) {
                    firstLine = false;
                    processFirstLine(line, primarySeparator);
                } else {
                    processLine(line, primarySeparator, nugget, writeError, quizName);
                    if (errCount == 10 && writeError) {
                        writeError = false;
                        System.out.println("Too many errors, further errors will fail silently for processing of " + fileName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            String current = mapper.writeValueAsString(root);
            System.out.println(current.substring(0,Math.min(50,current.length())) + " ... " + current.substring(Math.max(0, current.length() - 150)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void processFirstLine(String line, String separator) {
        props = line.split(Pattern.quote(separator));
        propsCount = props.length;
        errCount = 0;
        System.out.println(line + " > " + props.length);
    }

    private void processLine(String line, String primary, Map<String, Object> nugget, boolean writeError, String quizName) {
        String[] items = line.split(Pattern.quote(primary));
        if (items.length != propsCount) {
            errCount++;
            if (writeError) {
                System.out.println("Error on line: " + line + " : " + items.length);
            }
            return;
        }
//            {   "swedish": ["verkligen"],
//                "genki": ["17"],
//                "writing": ["そうか"],
//                "english": ["really"],
//                "reading": ["そうか"],
//                "state": "",
//                "id": "12x82",
//                "type": ["interjection"]
//            }
//            {
//                "quiz": "Den japanska floran",
//                "id": "djf1",
//                "type": ["quiz"],
//                "question": "Vilken växts blomning firas under högtiden Hanami?",
//                "correct": "körsbärsträd",
//                "incorrect" : ["näsduksträd", "ginkgo", "svinröv", "kryptomera", "japansk begonia", "astilbe", "magnolia"]
//        }
        String questionReading = "";
        String questionWriting = "";
        String questionSwedish = "";
        String kll = "";
        String genki = "";
        String id = "";
        String type = "";
//        System.out.println("Parsing line");
        for (int i = 0; i < items.length; i++) {
            switch (props[i]) {
                case "id":
                    id = generateId(items[i]);
                    break;
                case "state":
                    if ("hidden".equals(items[i])) {
                        return;
                    }
                    break;
                case "reading":
                    questionReading = items[i];
                    break;
                case "writing":
                    questionWriting = items[i];
                    break;
                case "swedish":
                    questionSwedish = items[i];
                    break;
                case "type":
                    type = items[i];
                    break;
                case "genki":
                    genki = items[i];
                case "kll":
                    kll = items[i];
            }
        }
        if (!"verb".equals(type)) {
            return;
        }
        System.out.println("Verb found");
        if (kll.equals("") && genki.equals("")) {
            return;
        }
        System.out.println("Genki or KLL");
        nugget.put("id", generateId(id));
        nugget.put("quiz", "Japansk grammatik, " + quizName);
        nugget.put("type", Collections.singletonList("quiz"));
        nugget.put("question", "Ange rätt " + quizName + " för " + questionWriting + " (" + questionReading + " =  " + questionSwedish + ")!");
        System.out.println(questionWriting);
        String correct = new Verb(questionWriting).asTeForm();
        nugget.put("correct", correct);
        List<String> incorrect = new ArrayList<>();
        String u = Verb.forceU(questionWriting).asTeForm();
        if (!u.equals(correct)) {
            incorrect.add(u);
        }
        String ru = Verb.forceRu(questionWriting).asTeForm();
        if (!ru.equals(correct)) {
            incorrect.add(ru);
        }
        String suru = Verb.forceSuru(questionWriting).asTeForm();
        if (!suru.equals(correct)) {
            incorrect.add(suru);
        }
        String kuru = Verb.forceKuru(questionWriting).asTeForm();
        if (!kuru.equals(correct)) {
            incorrect.add(kuru);
        }
        if (incorrect.size() == 2) {
            incorrect.add(new Verb(questionReading).asDictionaryForm());
        }
        nugget.put("incorrect", incorrect);
        root.add(nugget);
        //System.out.println(line + " : " + items.length);
    }

    private String generateId(String id) {
        idCount ++;
        if (id.equals("")) {
            return prefix + idCount;
        } else {
            return prefix + id;
        }
    }
}
