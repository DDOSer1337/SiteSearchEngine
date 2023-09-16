package searchengine.Busines.AddOrUpdatePage;

import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import searchengine.Busines.LemmaCreator;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.io.IOException;
import java.util.List;

import static searchengine.services.IndexingImpl.atomicBoolean;

@RequiredArgsConstructor
public class PageIndexing {
    @Autowired
    private final IndexRepository indexRepository;
    @Autowired
    private final PageRepository pageRepository;
    @Autowired
    private final LemmaRepository lemmaRepository;
    @Autowired
    private final SiteRepository siteRepository;

    private String url;

    public void pageIndexing(){
        Connection connection = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT " +
                        "5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com");
        try {
            Page page = new Page(url,connection.get(),"domain",new Site(),connection.execute().statusCode());
            if (!pageExist(page,page.getSiteId())){
                pageRepository.save(page);
                page = pageRepository.findByPath(page.getPath());
                List<Lemma> list = getLemmas(connection, page.getSiteId());
                for (Lemma lemma : list) {
                    if (atomicBoolean.get() && lemma != null) {
                        indexCreator(page, lemma);
                    }
                }
            }
            else {
                pageRepository.deleteByPath(page.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean pageExist(Page page, Site site) {
        return pageRepository.existsByPathAndSiteId(page.getPath(), site) && page.getPath().startsWith("/");
    }
    private List<Lemma> getLemmas(Connection connection, Site site) throws IOException {
        LemmaCreator lemmaCreator = new LemmaCreator(lemmaRepository, connection.get(), site);
        lemmaCreator.createLemmas();
        return lemmaCreator.getListLemmas();
    }

    private void indexCreator(Page page, Lemma lemma) {
        Index index = new Index(page, lemma);
        if (!indexRepository.existsByLemmaIdAndPageId(lemma, page)) {
            indexRepository.save(index);
        } else {
            indexRepository.upRank(page.getId(), lemma.getId());
        }
    }
}
