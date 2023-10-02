package searchengine.services.QueryProcessing;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.Arrays;

public class Lucene {
    private String word;
    private String[] l = {"от", "на", "об", "он", "до", "по", "не", "за", "со", "мы", "вы", "ли", "но",
            "ты", "во", "от", "до", "иза", "для", "как", "что", "оно", "чем", "она", "кто", "они", "под",
            "его", "сам", "мой", "вот", "тут", "без", "где"};

    public Lucene(String word) {
        this.word = word;
    }

    public LuceneMorphology getLuceneMorphology() throws IOException {
        if (isNotPartOfSpeech() && isNotOneLetter() && isNotFunctionalPartsOfSpeech()) {
            return isRussian() ? new RussianLuceneMorphology()
                    : isEnglish() ? new EnglishLuceneMorphology() : null;
        }
        else {
            return null;
        }
    }

    boolean isRussian() {
        return (word.matches("[а-яА-Я]+"));
    }

    boolean isEnglish() {
        return (word.matches("[a-zA-Z]+"));
    }

    public boolean isNotPartOfSpeech() {
        return !(word.endsWith("Н") || word.endsWith("МЕЖД") || word.endsWith("СОЮЗ") || word.endsWith("ПРЕДЛ") ||
                word.endsWith("ЧАСТ") ||
                word.endsWith("PREP") || word.endsWith("ADJECTIVE") || word.endsWith("CONJ") ||
                word.endsWith("ARTICLE") || word.endsWith("ADVERB"));
    }
    public boolean isNotOneLetter(){
        return word.length() != 1;
    }
    private boolean isNotFunctionalPartsOfSpeech(){
        return !Arrays.stream(l).toList().contains(word);
    }

}