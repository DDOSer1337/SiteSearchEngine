package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.Busines.LinkHandling.LinkParser;
import searchengine.config.SitesList;
import searchengine.dto.result.FailedResult;
import searchengine.dto.result.Result;
import searchengine.dto.result.SuccessResult;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.Interface.Indexing;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class IndexingImpl implements Indexing {
    private final SitesList sitesList;
    public static AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    private final LinkParser linkParser;
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
        Result result = new Result();
        result.setResult(atomicBoolean.get());
        if (result.isResult()){
            atomicBoolean.getAndSet(false);
            SuccessResult successResult = new SuccessResult();
            successResult.setResult(result);
            return ResponseEntity.status(HttpStatus.OK).body(successResult);
        }
        else {
            FailedResult failedResult = new FailedResult();
            failedResult.setResult(result);
            failedResult.setError("indexing not started");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(failedResult);
        }
    }

    @Override
    public ResponseEntity<?> start() {
        Result result = new Result();
        if (!atomicBoolean.get()){
            result.setResult(true);
            linkParser.startParse();
            SuccessResult successResult = new SuccessResult();
            successResult.setResult(result);
            return ResponseEntity.status(HttpStatus.OK).body(successResult);
        }else {
            result.setResult(false);
            FailedResult failedResult = new FailedResult();
            failedResult.setResult(result);
            failedResult.setError("indexing started");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(failedResult);
        }
    }
}
