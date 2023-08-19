package searchengine.services.Interface;

public interface SearchEngine {
    void findIn(String siteName,String[] word);
    void findAll(String[] word);
}
