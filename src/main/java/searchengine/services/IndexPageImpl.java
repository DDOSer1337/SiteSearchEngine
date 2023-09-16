package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.Busines.AddOrUpdatePage.PageIndexing;
import searchengine.services.Interface.IndexPage;

@Service
@RequiredArgsConstructor
public class IndexPageImpl implements IndexPage {
    private boolean isIndexing = false;

    public ResponseEntity<?> indexPage() {
        if (!isIndexing) {
            PageIndexing addOrUpdatePage;
        }
        return null;
    }
}
