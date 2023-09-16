package searchengine.Busines.Search;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.lucene.morphology.LuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.Busines.Lucene;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.search.Data;
import searchengine.model.Index;
import searchengine.model.Page;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Getter
@Setter
public class SearchByWord {
    @Autowired
    private IndexRepository indexRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    @Autowired
    private SitesList sitesList;
    private String[] word;
    private String url;
    private List<Data> dataList;
    private int count;

    public void startSearch() {
        dataList = new ArrayList<>();
        for (String w : word) {
            if (!w.isEmpty()) {
                try {
                    LuceneMorphology luceneMorphology = new Lucene(w.toLowerCase(Locale.ROOT)).getLuceneMorphology();
                    String correctWord = luceneMorphology.getNormalForms(w.toLowerCase(Locale.ROOT)).get(0);
                    if (url != null) {
                        indexRepository.findFirst10ByLemmaId_Lemma(w.toLowerCase(Locale.ROOT));
                        dataList.addAll(getData(correctWord, url));
                    } else {
                        for (Site site : sitesList.getSites()) {
                            //Optional<Index> indices = indexRepository.findFirst10ByLemmaId_LemmaAndLemmaId_SiteId_Url(correctWord, site.getUrl());
                            dataList.addAll(getData(correctWord, site.getUrl()));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<Data> getData(String correctWord, String url) {
        System.out.println("getData");
        List<Data> list = new ArrayList<>();
        Optional<Integer> count = Optional.ofNullable(indexRepository.getRank(url, correctWord));
        Optional<searchengine.model.Site> site = Optional.ofNullable(siteRepository.findByUrl(url));
        Iterable<Index> i = indexRepository.findTop10ByLemmaId_lemmaAndPageId_SiteId_urlOrderByLemmaId_frequencyDesc(correctWord, url);
        AtomicReference<Float> allReliance = new AtomicReference<>(0f);
        if (site.isPresent()) {
            System.out.println("\n i " + i.toString() + "\n");
            i.forEach(index -> {Data data = new Data() ;
                Page page = index.getPageId();
                data.setSite(site.get().getUrl() + "/");
                data.setSiteName(site.get().getName());
                data.setUrl(page.getPath());
                data.setSnippet("<b>" + setSnippet(page.getContent(), correctWord) + "</b>");
                data.setTitle(setTitle(page.getContent()));
                float reliance = index.getRank();
                data.setRelevance(reliance);
                allReliance.set(allReliance.get() + reliance);
                list.add(data);
            });
        }
        for (Data data : list) {
            System.out.println("all allReliance:" + allReliance
                    + "\n data.getRelevance()" + data.getRelevance());
            data.setRelevance(data.getRelevance() / allReliance.get());
        }
        return list;
    }

    public List<Data> getFoundedData() {
        return dataList;
    }

    public int getCount() {
        return count;
    }

    private String setSnippet(String htmlCode, String wordToFind) {
        StringBuilder snippet = new StringBuilder();

        Document doc = Jsoup.parse(htmlCode);
        Elements elements = doc.getElementsContainingOwnText(wordToFind);

        for (Element element : elements) {
            snippet.append(element.outerHtml());
        }

        return snippet.toString();
    }

    private String setTitle(String content) {
        int start = content.indexOf("<title>");
        int end = content.indexOf("</title>");
        String result = content.substring(start, end + 8);
        return result;
    }

    private float setRelevance() {
        return 0.0f;
    }
}
