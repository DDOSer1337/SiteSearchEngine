package searchengine.services.Interface;

import org.springframework.http.ResponseEntity;

import java.util.concurrent.atomic.AtomicBoolean;


public interface Indexing {
    AtomicBoolean isIndexing = new AtomicBoolean(false);
    ResponseEntity<?> stop();
    ResponseEntity<?> start();
}
