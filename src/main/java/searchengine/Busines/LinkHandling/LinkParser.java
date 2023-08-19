package searchengine.Busines.LinkHandling;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import searchengine.dto.FailedResult;
import searchengine.dto.Result;
import searchengine.model.Site;
import searchengine.repositories.SiteRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static searchengine.services.IndexingImpl.atomicBoolean;

@RequiredArgsConstructor
public class LinkParser {
    private String domain;
    private Set<String> verifiedLinks = Collections.synchronizedSet(new HashSet<>());
    private final String url;
    @Autowired
    private SiteRepository siteRepository;
    List<LinkCrawler> list;

    public ResponseEntity<?> startParse() {
        Result result = new Result();
        result.setResult(atomicBoolean.get());
        if (!result.isResult() && isURL()) {
            atomicBoolean.getAndSet(true);
            domain = url.split("/")[2];
            Site site = new Site(url, domain);
            if (siteRepository.isExist(site.getName())) {
                siteRepository.deleteByName(site.getName());
            }
            siteRepository.save(site);
            LinkCrawler linkCrawler = new LinkCrawler(domain, url, verifiedLinks, site);
            linkCrawler.compute();
            list.add(linkCrawler);
            result.setResult(true);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } else {
            FailedResult failedResult = new FailedResult();
            failedResult.setResult(result);
            failedResult.setError("");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(failedResult);
        }
    }

    public ResponseEntity<?> stopParse() {
        Result result = new Result();
        result.setResult(atomicBoolean.get());
        if (result.isResult()) {
            atomicBoolean.getAndSet(false);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } else {
            FailedResult failedResult = new FailedResult();
            failedResult.setResult(result);
            failedResult.setError("");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(failedResult);
        }
    }

    public boolean isURL() {
        return url != null && (url.matches("^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"));
    }

}
