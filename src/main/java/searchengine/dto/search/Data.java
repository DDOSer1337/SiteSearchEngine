package searchengine.dto.search;

@lombok.Data
public class Data {
    String site;
    String siteName;
    String url;
    String title;
    String snippet;
    float relevance;
}
