package searchengine.services.QueryProcessing.LinkHandling;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.services.QueryProcessing.LemmaCreator;
import searchengine.model.Enum.SiteStatus;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveAction;

import static searchengine.controllers.ApiController.isIndexing;

@RequiredArgsConstructor
@Setter
@Service
public class LinkParser extends RecursiveAction {
    private String domain, currentLink;
    private Set<String> verifiedLinks;
    private Site site;
    @Autowired
    private final SiteRepository siteRepository;
    @Autowired
    private final PageRepository pageRepository;
    @Autowired
    private final LemmaRepository lemmaRepository;
    @Autowired
    private final IndexRepository indexRepository;


    @Override
    protected void compute() {
        if (isIndexing.get() && !verifiedLinks.contains(currentLink)) {
            linkChecking();
        }
    }

    private void linkChecking() {
        Connection connection = Jsoup.connect(currentLink)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT " +
                        "5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com");
        try {
            List<Element> links = connection.get().select("a[href]");
            for (Element link : links) {
                if (!verifiedLinks.contains(link.baseUri())) {
                    String newLink = link.attr("abs:href");
                    recursiveActionFork(newLink, connection);
                }
            }
            verifiedLinks.add(currentLink);
        } catch (IOException e) {
            siteRepository.UpdateStatusByName(site.getName(), SiteStatus.FAILED.toString());
            e.printStackTrace();
        }
    }

    private void recursiveActionFork(String newLink, Connection connection) throws IOException {
        if (isIndexing.get() && !newLink.endsWith(".jpg")) {
            Optional<Site> site = Optional.ofNullable(siteRepository.findByName(domain));
            if (isIndexing.get() && site.isPresent()) {
                checkPage(newLink, connection, site.get());
            }
        }
    }

    private void checkPage(String newLink, Connection connection, Site site) throws IOException {
        Page page = new Page(newLink, connection.get(), domain, site, connection.execute().statusCode());
        if (!pageExist(page) && page.getPath() != null && page.getPath().startsWith("/")) {
            savePageAndGetListLemma(newLink, connection, site, page);
        }
    }
    private boolean pageExist(Page page) {
        return domain.startsWith("www.")
                ? pageRepository.existsByPathAndSiteId_name(page.getPath(), domain.substring(4))
                : pageRepository.existsByPathAndSiteId_name(page.getPath(), domain);
    }

    private void savePageAndGetListLemma(String newLink, Connection connection, Site site, Page page) throws IOException {
        pageRepository.save(page);
        page = pageRepository.findByPathAndSiteId_Name(page.getPath(), site.getName());
        List<Lemma> list = getLemmas(connection, site);
        for (Lemma lemma : list) {
            createIndexAndFork(newLink, page, lemma);
        }
    }
    private List<Lemma> getLemmas(Connection connection, Site site) throws IOException {
        LemmaCreator lemmaCreator = new LemmaCreator(lemmaRepository, connection.get(), site, true);
        lemmaCreator.createLemmas();
        return lemmaCreator.getListLemmas();
    }

    private void createIndexAndFork(String newLink, Page page, Lemma lemma) {
        if (isIndexing.get() && lemma != null) {
            indexCreator(page, lemma);
            forking(newLink);
        }
    }

    private void indexCreator(Page page, Lemma lemma) {
        if (!indexRepository.existsByLemmaIdAndPageId(lemma, page)) {
            indexRepository.save(new Index(page, lemma));
        } else {
            indexRepository.upRank(page.getId(), lemma.getId());
        }
    }

    private void forking(String newLink) {
        if (isIndexing.get()) {
            LinkParser linkCrawler = new LinkParser(siteRepository, pageRepository, lemmaRepository, indexRepository);
            linkCrawler.setCurrentLink(newLink);
            linkCrawler.setDomain(domain);
            linkCrawler.setCurrentLink(newLink);
            linkCrawler.setSite(site);
            linkCrawler.setVerifiedLinks(verifiedLinks);
            linkCrawler.fork();
        }
    }
}
