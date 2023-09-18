package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.Busines.AddOrUpdatePage.PageIndexer;
import searchengine.services.Interface.IndexPage;

@Service
@RequiredArgsConstructor
public class IndexPageImpl implements IndexPage {
    private final PageIndexer pageIndexer;

    @Override
    public ResponseEntity<?> AddOrUpdatePage(String url) {
        return pageIndexer.AddOrUpdatePage(url);
    }
}
