package searchengine.services;

public interface SearchEngine {
    void findIn(String siteName,String[] word);
    void findAll(String[] word);
}
