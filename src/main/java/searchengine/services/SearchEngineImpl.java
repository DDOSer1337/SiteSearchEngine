package searchengine.services;

import org.springframework.stereotype.Service;
import searchengine.services.Interface.SearchEngine;

@Service
public class SearchEngineImpl implements SearchEngine {
    @Override
    public void findIn(String siteName, String[] word) {
    }

    @Override
    public void findAll(String[] word) {

    }
}
