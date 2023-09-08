package searchengine.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Page;

import java.util.Optional;


@Repository
public interface PageRepository extends CrudRepository<Page,Long> {

    boolean existsByPathAndSiteId_id(String path,int id);

    @Query(value ="Select `content` from skillbox.pages where WHERE path = :path and sites_id = :sitesID",nativeQuery = true)
    Integer getContent(@Param("path") String path, @Param("sitesID") int sitesID);

    Page findByPath(String path);
    Page findBySiteId_url(String url);

    @Transactional
    long deleteByPath(String path);

//    @Query(value ="Select Count(*) from skillbox.pages as p Join skillbox.sites as s on p.sites_id = s.id where s.name = :siteName",nativeQuery = true)
//    Integer getPageCount(@Param("siteName") String siteName);

    long countBySiteId_Name(String siteName);
}