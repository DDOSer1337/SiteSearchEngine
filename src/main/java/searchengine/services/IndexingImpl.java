package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import searchengine.Busines.LinkHandling.LinkParser;
import searchengine.services.Interface.Indexing;

import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class IndexingImpl implements Indexing {
    String url;
    public static AtomicBoolean atomicBoolean;

    LinkParser linkParser = new LinkParser(url);
    @Override
    public ResponseEntity<?> stop() {
        return linkParser.stopParse();
    }

    @Override
    public ResponseEntity<?> start() {
        return linkParser.startParse();
    }
}
