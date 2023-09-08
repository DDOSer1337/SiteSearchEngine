package searchengine.Busines.Search;


import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

@RequiredArgsConstructor
public class Search {
    private final String[] word;
    private final String url;
    @Autowired
    private IndexRepository indexRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    private List<Data> dataList;
    private final SitesList sitesList;
    private int count;

    public void startSearch() {
        for (String w : word) {
            if (!w.isEmpty()) {
                System.out.println(w);
                try {
                    LuceneMorphology luceneMorphology = new Lucene(w).getLuceneMorphology();
                    String correctWord = luceneMorphology.getNormalForms(w).get(0);
                    if (url != null) {
                        indexRepository.findFirst10ByLemmaId_Lemma(w);
                        dataList.add(getData(correctWord, url));
                    } else {
                        for (Site site : sitesList.getSites()) {
                            Iterable<Index> indices = indexRepository.findFirst10ByLemmaId_LemmaAndLemmaId_SiteId_Url(correctWord, site.getUrl());
                            dataList.add(getData(correctWord, site.getUrl()));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Data getData(String correctWord, String url) {
        Data data = new Data();
        count = indexRepository.getRank(url, correctWord);
        searchengine.model.Site site = siteRepository.findByUrl(url);
        Page page = pageRepository.findBySiteId_url(url);
        data.setUrl(url);
        data.setSite(site.getName());
        data.setSnippet(setSnippet(page.getContent()));
        data.setTitle(setTitle(page.getContent()));
        data.setRelevance(0.0f);
        return data;
    }

    public List<Data> getFoundedData() {
        return dataList;
    }

    public int getCount() {
        return count;
    }

    private String setSnippet(String content) {
        String result = "";
        return result;
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
