package searchengine.services.Interface;

import org.springframework.http.ResponseEntity;

public interface SearchEngine {
    ResponseEntity<?> search(String siteName,String[] word,int offset);
}
