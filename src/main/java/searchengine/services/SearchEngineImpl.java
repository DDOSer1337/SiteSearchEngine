package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.Busines.Search.SearchByWord;
import searchengine.dto.result.FailedResult;
import searchengine.dto.result.Result;
import searchengine.dto.searchByWord.SuccessSearchResult;
import searchengine.services.Interface.SearchEngine;

@Service
@RequiredArgsConstructor
public class SearchEngineImpl implements SearchEngine {
    private final SearchByWord searchByWord;

    @Override
    public ResponseEntity<?> search(String siteName, String[] word) {
        Result result = new Result();
        if (!word[0].isEmpty()){
            result.setResult(true);
            SuccessSearchResult successResult = new SuccessSearchResult();
            successResult.setResult(result.isResult());
            searchByWord.setWord(word);
            searchByWord.setUrl(siteName);
            searchByWord.startSearch();
            successResult.setCount(searchByWord.getCount());
            successResult.setData(searchByWord.getFoundedData());
            return ResponseEntity.status(HttpStatus.OK).body(successResult);
        }
        else {
            result.setResult(false);
            FailedResult failedResult = new FailedResult();
            failedResult.setResult(result);
            failedResult.setError("Задан неверный поисковый запрос");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(failedResult);
        }
    }
}
