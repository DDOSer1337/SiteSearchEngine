package searchengine.services.QueryProcessing.SearchByWord;


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
import searchengine.services.QueryProcessing.Lucene;
import searchengine.config.SitesList;
import searchengine.dto.searchByWord.Data;
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
    private int count = 0;
    private float allReliance = 0f;

    public void startSearch() {
        count = 0;
        allReliance = 0f;
        dataList = new ArrayList<>();
        for (String w : word) {
            if (!w.isEmpty()) {
                try {
                    LuceneMorphology luceneMorphology = new Lucene(w.toLowerCase(Locale.ROOT)).getLuceneMorphology();
                    String correctWord = luceneMorphology.getNormalForms(w.toLowerCase(Locale.ROOT)).get(0);
                    dataList.addAll(getListData(correctWord));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<Data> getListData(String correctWord) {
        Optional<Integer> count;
        List<Index> indices;
        if (url == null) {
            count = indexRepository.getRankOnAllSites(correctWord);
            indices = indexRepository.findAllByLemmaId_LemmaOrderByLemmaId_frequency(correctWord);
        } else {
            count = indexRepository.getRankOnOneSite(url, correctWord);
            indices = indexRepository.findAllByLemmaId_lemmaAndPageId_SiteId_urlOrderByLemmaId_frequency(correctWord, url);
        }
        return new ArrayList<>(dataListCreator(indices, count, correctWord));
    }

    private List<Data> dataListCreator(List<Index> indices, Optional<Integer> getCount, String correctWord) {
        List<Data> list = new ArrayList<>();
        getCount.ifPresent(integer -> count = integer);
        System.out.println(indices.size());
        for (Index index : indices){
            System.out.println("\n\n"+index.getPageId().getSiteId().getName()+"\n\n");
            Optional<searchengine.model.Site> site = Optional.ofNullable(siteRepository.findByUrl(index.getPageId().getSiteId().getUrl()));
            if (site.isPresent()) {
                float reliance = index.getRank();
                Page page = index.getPageId();
                allReliance+= reliance;
                list.add(dataCreator(correctWord, site.get(), reliance, page));
            }
        }
        for (Data data : list) {
            data.setRelevance(data.getRelevance() / allReliance);
        }
        return list;
    }

    private Data dataCreator(String correctWord, searchengine.model.Site site, float reliance, Page page) {
        Data data = new Data();
        data.setSite(site.getUrl());
        data.setSiteName(site.getName());
        String path = page.getPath();
        if (!path.startsWith("/")){
            path="/"+path;
        }
        data.setUrl(path);
        data.setSnippet("<b>" + setSnippet(page.getContent(), correctWord) + "</b>");
        data.setTitle(setTitle(page.getContent()));
        data.setRelevance(reliance);
        return data;
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
        return content.substring(start, end + 8);
    }
}
