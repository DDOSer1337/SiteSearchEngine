package searchengine.dto.searchByWord;

import java.util.List;

@lombok.Data
public class SuccessSearchResult {
    boolean result;
    int count;
    List<Data> data;
}
