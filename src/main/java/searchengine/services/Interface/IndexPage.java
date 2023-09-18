package searchengine.services.Interface;

import org.springframework.http.ResponseEntity;

public interface IndexPage {
    ResponseEntity<?> AddOrUpdatePage(String url);
}
