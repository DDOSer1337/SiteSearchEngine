package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.Busines.AddOrUpdatePage.PageIndexer;
import searchengine.services.Interface.IndexPage;

@Service
@RequiredArgsConstructor
public class IndexPageImpl implements IndexPage {
    private boolean isIndexing = false;

    public ResponseEntity<?> indexPage() {
        if (!isIndexing) {
            PageIndexer addOrUpdatePage;
        }
        return null;
    }
}
