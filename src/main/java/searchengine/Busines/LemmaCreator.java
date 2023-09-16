package searchengine.Busines;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import searchengine.Busines.Lucene;
import searchengine.model.Lemma;
import searchengine.model.Site;
import searchengine.repositories.LemmaRepository;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
public class LemmaCreator {
    @Autowired
    private final LemmaRepository lemmaRepository;
    private final Document document;
    private final Site site;
    private List<Lemma> list;

    public List<Lemma> getListLemmas() {
        return list;
    }

    public void createLemmas() {
        list = new ArrayList<>();
        List<String> allText = Arrays.stream(document.text().split(" ")).toList();
        for (String textWord : allText) {
            try {
                Lemma lemma = createLemma(textWord);
                if (lemma != null) {
                    Optional<Lemma> optionalLemma = lemmaRepository.findByLemma(lemma.getLemma());
                    if (optionalLemma.isPresent()) {
                        lemma = optionalLemma.get();
                        list.add(lemma);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Lemma createLemma(String textWord) throws IOException {
        String word2lower = textWord.toLowerCase(Locale.ROOT).trim();
        LuceneMorphology luceneMorphology = new Lucene(word2lower).getLuceneMorphology();
        if (luceneMorphology != null && textWord.matches("^([a-zа-яё]+|\\d+)$")) {
            String word = luceneMorphology.getNormalForms(word2lower).get(0);
            Lemma lemma = new Lemma();
            lemma.setLemma(word);
            lemma.setSiteId(site);
            if (lemmaRepository.existsByLemmaAndSiteId(lemma.getLemma(), lemma.getSiteId())) {
                lemmaRepository.updateFrequency(lemma.getLemma(), lemma.getSiteId().getId());
            } else {
                lemma.setFrequency(1);
                lemmaRepository.save(lemma);
            }
            return lemma;
        }
        return null;
    }
}
