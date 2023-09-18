package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.Busines.LinkHandling.LinkParser;
import searchengine.dto.result.FailedResult;
import searchengine.dto.result.Result;
import searchengine.dto.result.SuccessResult;
import searchengine.services.Interface.Indexing;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class IndexingImpl implements Indexing {
    public static AtomicBoolean isIndexing = new AtomicBoolean(false);
    private final LinkParser linkParser;

    @Override
    public ResponseEntity<?> stop() {
        Result result = new Result();
        result.setResult(isIndexing.get());
        if (result.isResult()){
            isIndexing.getAndSet(false);
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
        if (!isIndexing.get()){
            result.setResult(true);
            new Thread(linkParser::startParse).start();
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
