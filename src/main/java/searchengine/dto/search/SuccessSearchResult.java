package searchengine.dto.search;

import searchengine.dto.Result;

import java.util.List;

@lombok.Data
public class SuccessSearchResult {
    Result result;
    int count;
    List<Data> data;
}
