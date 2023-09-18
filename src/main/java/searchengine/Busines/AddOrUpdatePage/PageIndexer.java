package searchengine.Busines.AddOrUpdatePage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.Busines.LemmaCreator;
import searchengine.dto.result.FailedResult;
import searchengine.dto.result.Result;
import searchengine.dto.result.SuccessResult;
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

@Service @Setter @Getter
@RequiredArgsConstructor
public class PageIndexer{
    private int count = 0;
    @Autowired
    private final IndexRepository indexRepository;
    @Autowired
    private final PageRepository pageRepository;
    @Autowired
    private final LemmaRepository lemmaRepository;
    @Autowired
    private final SiteRepository siteRepository;

    private Site site;
    private String url;
    private String domain;

    public ResponseEntity<?> AddOrUpdatePage(String url) {
        this.url = url;
        domain = url.split("/")[2];
        String siteName = getSiteName(domain);
        if (siteRepository.existsByName(siteName)){
            site = siteRepository.findByName(siteName);
            new Thread(this::pageIndexing).start();
            Result result = new Result();
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        else {
            Result result = new Result();
            result.setResult(false);
            FailedResult failedResult = new FailedResult();
            failedResult.setResult(result);
            failedResult.setError("Данная страница находится за пределами сайтов, указанных в конфигурационном файле");
            return ResponseEntity.status(HttpStatus.OK).body(failedResult);
        }
    }

    public void pageIndexing() {
        if (isURL(url)) {
            Connection connection = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT " +
                            "5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com");
            try {
                Page page = new Page(url, connection.get(), domain, site, connection.execute().statusCode());
                saveOrDeletePage(page);
                page = pageRepository.findByPath(page.getPath());
                indexing(connection, page);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getSiteName(String domain) {
        String siteName = "";
        if (domain.startsWith("www.")) {
            siteName = domain.substring(4);
        } else {
            siteName = domain;
        }
        return siteName;
    }

    private void saveOrDeletePage(Page page) {
        if (!pageExist(page, page.getSiteId())) {
            pageRepository.save(page);
        } else {
            pageRepository.deleteByPath(page.getPath());
            pageRepository.save(page);
        }
    }

    private boolean pageExist(Page page, Site site) {
        return pageRepository.existsByPathAndSiteId(page.getPath(), site) && page.getPath().startsWith("/");
    }

    private void indexing(Connection connection, Page page) throws IOException {
        List<Lemma> list = getLemmas(connection, page.getSiteId());
        for (Lemma lemma : list) {
            indexCreator(page, lemma);
            if (count % 10 == 0) {
                System.out.println(count);
            }
        }
    }

    private List<Lemma> getLemmas(Connection connection, Site site) throws IOException {
        LemmaCreator lemmaCreator = new LemmaCreator(lemmaRepository, connection.get(), site);
        lemmaCreator.createLemmas();
        return lemmaCreator.getListLemmas();
    }

    private void indexCreator(Page page, Lemma lemma) {
        Index index = new Index(page, lemma);
        count++;
        if (!indexRepository.existsByLemmaIdAndPageId(lemma, page)) {
            indexRepository.save(index);
        } else {
            indexRepository.upRank(page.getId(), lemma.getId());
        }
    }

    private boolean isURL(String url) {
        return url != null && (url.matches("^(https?)://(www.)?[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"));
    }
}
