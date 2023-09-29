package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.Busines.SearchByWord.SearchByWord;
import searchengine.dto.result.FailedResult;
import searchengine.dto.result.Result;
import searchengine.dto.searchByWord.Data;
import searchengine.dto.searchByWord.SuccessSearchResult;
import searchengine.services.Interface.SearchEngine;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchEngineImpl implements SearchEngine {
    private final SearchByWord searchByWord;

    @Override
    public ResponseEntity<?> search(String siteName, String[] word, int offset) {
        Result result = new Result();
        if (!word[0].isEmpty()) {

            result.setResult(true);
            SuccessSearchResult successResult = new SuccessSearchResult();
            successResult.setResult(result.isResult());
            searchByWord.setWord(word);
            searchByWord.setUrl(siteName);
            searchByWord.startSearch();
            successResult.setCount(searchByWord.getCount());
            List<Data> data = new ArrayList<>();
            List<Data> foundedData = searchByWord.getFoundedData();
            for (int i = 0; i < 10; i++) {
                int point = offset + i;
                if (foundedData.size() > point) {
                    data.add(foundedData.get(point));
                }
            }
            successResult.setData(data);
            return ResponseEntity.status(HttpStatus.OK).body(successResult);
        } else {
            result.setResult(false);
            FailedResult failedResult = new FailedResult();
            failedResult.setResult(result);
            failedResult.setError("Задан неверный поисковый запрос");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(failedResult);
        }
    }
}
