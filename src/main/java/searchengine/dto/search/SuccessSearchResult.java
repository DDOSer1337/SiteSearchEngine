package searchengine.dto.search;

import searchengine.dto.result.Result;

import java.util.List;

@lombok.Data
public class SuccessSearchResult {
    boolean result;
    int count;
    List<Data> data;
}
