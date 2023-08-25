package searchengine.services;

import org.springframework.http.ResponseEntity;

public interface Indexing {
    ResponseEntity<?> stop();
    ResponseEntity<?> start();
}
