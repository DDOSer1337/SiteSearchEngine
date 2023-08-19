package searchengine.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Page;


@Repository
public interface PageRepository extends CrudRepository<Page,Long> {
    @Query(value ="SELECT CASE WHEN frequency > 0 THEN true ELSE false END FROM skillbox.pages WHERE path = %:path% and sites_id = :sitesID",nativeQuery = true)
    boolean isExist(@Param("path") String lemma, @Param("sitesID") int sitesID);

    @Query(value ="Select `content` from skillbox.pages where WHERE path = %:path% and sites_id = :sitesID",nativeQuery = true)
    Integer getContent(@Param("path") String path, @Param("sitesID") int sitesID);

    @Modifying
    @Query(value ="DELETE FROM skillbox.pages WHERE path = %:path% AND sites_id = :sitesID",nativeQuery = true)
    void deletePageByPath();

    @Query(value ="Select Count(*) from skillbox.pages as p Join skillbox.sites as s on p.sites_id = s.id where s.name = :siteName",nativeQuery = true)
    Integer getPageCount(@Param("siteName") String siteName);

    @Query(value ="SELECT Count(*) FROM skillbox.pages")
    Integer getAllPageCount ();
}
