package searchengine.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Page;

@Repository
public interface PageRepository extends CrudRepository<Page,Long> {

    boolean existsByPathAndSiteId_name(String path, String siteName);

    Page findByPath(String path);
    Page findBySiteId_url(String url);

    @Transactional
    long deleteByPath(String path);

    long countBySiteId_Name(String siteName);
}