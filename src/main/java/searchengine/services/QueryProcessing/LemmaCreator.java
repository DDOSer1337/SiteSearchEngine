package searchengine.services.QueryProcessing;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import searchengine.model.Lemma;
import searchengine.model.Site;
import searchengine.repositories.LemmaRepository;
import searchengine.services.IndexingImpl;

import java.io.IOException;
import java.util.*;

import static searchengine.controllers.ApiController.isIndexing;

@RequiredArgsConstructor
public class LemmaCreator {
    @Autowired
    private final LemmaRepository lemmaRepository;
    private final Document document;
    private final Site site;
    private List<Lemma> list;
    private final boolean canStop;

    public List<Lemma> getListLemmas() {
        return list;
    }

    public void createLemmas() {
        list = new ArrayList<>();
        List<String> allText = Arrays.stream(document.text().split(" ")).toList();
        allText.forEach(word -> {
            Lemma lemma = createLemma(word);
            if (lemma != null) {
                if (canStop) {
                    if (isIndexing.get()) {
                        Optional<Lemma> optionalLemma = lemmaRepository.findByLemmaAndSiteId_name(lemma.getLemma(), site.getName());
                        if (optionalLemma.isPresent()) {
                            lemma = optionalLemma.get();
                            list.add(lemma);
                        }
                    }
                } else {
                    Optional<Lemma> optionalLemma = lemmaRepository.findByLemmaAndSiteId_name(lemma.getLemma(), site.getName());
                    if (optionalLemma.isPresent()) {
                        lemma = optionalLemma.get();
                        list.add(lemma);
                    }
                }
            }
        });
    }

    private Lemma createLemma(String textWord) {
        String word2lower = textWord.toLowerCase(Locale.ROOT).trim();
        LuceneMorphology luceneMorphology = null;
        try {
            luceneMorphology = new Lucene(word2lower).getLuceneMorphology();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (luceneMorphology != null && textWord.matches("^([a-zа-яё]+|\\d+)$")) {
            String word = luceneMorphology.getNormalForms(word2lower).get(0);
            Lemma lemma = new Lemma();
            lemma.setLemma(word);
            lemma.setSiteId(site);
            if (!lemmaRepository.existsByLemmaAndSiteId_name(lemma.getLemma(), lemma.getSiteId().getName())) {
                lemma.setFrequency(1);
                lemmaRepository.save(lemma);
            } else {
                lemmaRepository.updateFrequency(lemma.getLemma(), lemma.getSiteId().getId());
            }
            return lemma;
        }
        return null;
    }
}
