package searchengine.Busines.LinkHandling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.Busines.LemmaCreator;
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
import static searchengine.services.Interface.Indexing.isIndexing;

@RequiredArgsConstructor
@Getter
@Setter
@Service
public class LinkCrawler extends RecursiveAction {
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
            exceptionSite(site);
            e.printStackTrace();
        }
    }

    private void recursiveActionFork(String newLink, Connection connection) throws IOException {
        Optional<Site> site = Optional.ofNullable(siteRepository.findByName(domain.substring(4)));
        if (site.isPresent()) {
            Page page = new Page(newLink, connection.get(), domain, site.get(), connection.execute().statusCode());
            if (!pageExist(page, site.get())) {
                pageRepository.save(page);
                page = pageRepository.findByPath(page.getPath());
                List<Lemma> list = getLemmas(connection, site.get());
                for (Lemma lemma : list) {
                    if (isIndexing.get() && lemma != null) {
                        indexCreator(newLink, site.get(), page, lemma);
                    }
                }
            }
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

    private void indexCreator(String newLink, Site site, Page page, Lemma lemma) {
        Index index = new Index(page, lemma);
        if (!indexRepository.existsByLemmaIdAndPageId(lemma, page)) {
            indexRepository.save(index);
        } else {
            indexRepository.upRank(page.getId(), lemma.getId());
        }
        forking(newLink, site);
    }

    private void forking(String newLink, Site site) {
        LinkCrawler linkCrawler = new LinkCrawler(siteRepository, pageRepository, lemmaRepository, indexRepository);
        linkCrawler.setDomain(domain);
        linkCrawler.setCurrentLink(newLink);
        linkCrawler.setSite(site);
        linkCrawler.setVerifiedLinks(verifiedLinks);
        linkCrawler.fork();
    }

    private void exceptionSite(Site site) {
        siteRepository.UpdateErrorByName(site.getName(), SiteStatus.FAILED);
    }

}
