package searchengine.services.QueryProcessing.LinkHandling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.model.Enum.SiteStatus;
import searchengine.model.Site;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static searchengine.controllers.ApiController.isIndexing;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class SiteParser {
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
            String domain = getDomain(siteFromList.getUrl());
            if (siteRepository.existsByName(domain)){
                siteRepository.deleteByName(domain);
            }
        }
        for (searchengine.config.Site siteFromList : listSites) {
            String url = siteFromList.getUrl();
            if (isIndexing.get() && isURL(url)) {
                String domain = getDomain(url);
                Site site = new Site(url, domain);
                siteRepository.save(site);
                LinkParser newLinkParser = getLinkParser(siteFromList, domain, site);
                newLinkParser.compute();
                newLinkParser.invoke();
                if (newLinkParser.isDone()) {
                    System.out.println("isDone");
                    siteRepository.UpdateStatusByName(site.getName(), SiteStatus.INDEXED.toString());
                }
            }
        }
    }

    private String getDomain(String url) {
        String domain = url.split("/")[2];
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    private LinkParser getLinkParser(searchengine.config.Site siteFromList, String domain, Site site) {
        LinkParser newLinkParser = linkParser;
        newLinkParser.setDomain(domain);
        newLinkParser.setCurrentLink(siteFromList.getUrl());
        newLinkParser.setSite(site);
        newLinkParser.setVerifiedLinks(Collections.synchronizedSet(new HashSet<>()));
        return newLinkParser;
    }


    private boolean isURL(String url) {
        return url != null && url.matches("^(https?)://(www.)?[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    }

}
