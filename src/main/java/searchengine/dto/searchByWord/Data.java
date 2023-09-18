package searchengine.dto.searchByWord;

@lombok.Data
public class Data {
    String site;
    String siteName;
    String url;
    String title;
    String snippet;
    float relevance;
}
