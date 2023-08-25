package searchengine.Busines.LinkHandling;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.Result;
import searchengine.model.Site;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static searchengine.services.IndexingImpl.atomicBoolean;

@Service
@RequiredArgsConstructor
public class LinkParser {
    private String domain;
    private Set<String> verifiedLinks = Collections.synchronizedSet(new HashSet<>());
    private final SitesList sitesList;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;


    public void startParse() {
        List<searchengine.config.Site> listSites = sitesList.getSites();
        Result result = new Result();
        result.setResult(atomicBoolean.get());
        for (searchengine.config.Site siteFromList : listSites) {
            String url = siteFromList.getUrl();
            if (!result.isResult() && isURL(url)) {
                atomicBoolean.getAndSet(true);
                domain = url.split("/")[2];
                Site site = new Site(url, domain);
                if (siteRepository.isExist(site.getName())) {
                    siteRepository.deleteByName(site.getName());
                    System.out.println("Удалено " + site.getName());
                }
                siteRepository.save(site);
                LinkCrawler linkCrawler = new LinkCrawler(domain, url, verifiedLinks, site,siteRepository,pageRepository,lemmaRepository,indexRepository);
                linkCrawler.compute();
            }
        }
    }

    private boolean isURL(String url) {
        return url != null && (url.matches("^(https?)://(www.)?[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"));
    }

}
