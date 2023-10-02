package searchengine.services.QueryProcessing.LinkHandling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.model.Site;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.IndexingImpl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service @Getter @Setter
@RequiredArgsConstructor
public class SiteParser {
    private String domain;
    private Set<String> verifiedLinks = Collections.synchronizedSet(new HashSet<>());
    private final SitesList sitesList;
    @Autowired
    private final SiteRepository siteRepository;
    @Autowired
    private final PageRepository pageRepository;
    @Autowired
    private final LemmaRepository lemmaRepository;
    @Autowired
    private final IndexRepository indexRepository;
    private final LinkParser linkParser;


    public void startParse() {
        List<searchengine.config.Site> listSites = sitesList.getSites();
        for (searchengine.config.Site siteFromList : listSites) {
            String url = siteFromList.getUrl();
            if (IndexingImpl.isIndexing.get() && isURL(url)) {
                domain = url.split("/")[2];
                if (domain.startsWith("www.")) {
                    domain = domain.substring(4);
                }
                Site site = new Site(url, domain);
                if (siteRepository.existsByName(site.getName())) {
                    siteRepository.deleteByName(site.getName());
                }
                siteRepository.save(site);
                linkParser.setDomain(domain);
                linkParser.setCurrentLink(siteFromList.getUrl());
                linkParser.setSite(site);
                linkParser.setVerifiedLinks(verifiedLinks);
                linkParser.compute();
            }
        }
    }

    private boolean isURL(String url) {
        return url != null && (url.matches("^(https?)://(www.)?[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"));
    }

}
