package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.Busines.LinkHandling.LinkParser;
import searchengine.config.SitesList;
import searchengine.dto.FailedResult;
import searchengine.dto.Result;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class IndexingImpl implements Indexing {
    private final SitesList sitesList;
    public static AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    @Autowired
    private final SiteRepository siteRepository;
    @Autowired
    private final PageRepository pageRepository;
    @Autowired
    private final LemmaRepository lemmaRepository;
    @Autowired
    private final IndexRepository indexRepository;

    @Override
    public ResponseEntity<?> stop() {
        ResponseEntity<?> responseEntity;
        Result result = new Result();
        result.setResult(atomicBoolean.get());
        if (result.isResult()){
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(true);
            atomicBoolean.getAndSet(false);
        }
        else {
            FailedResult failedResult = new FailedResult();
            failedResult.setResult(result);
            failedResult.setError("Indexing not started");
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(failedResult);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> start() {
        Result result = new Result();
        result.setResult(true);
        ResponseEntity.status(HttpStatus.OK).body(result);
        if (!atomicBoolean.get()){
            result.setResult(true);
            LinkParser linkParser = new LinkParser(sitesList,siteRepository,pageRepository,lemmaRepository,indexRepository);
            linkParser.startParse();

            return ResponseEntity.status(HttpStatus.OK).body(result);
        }else {
            FailedResult failedResult = new FailedResult();
            failedResult.setResult(result);
            failedResult.setError("indexing started");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(failedResult);
        }
    }
}
