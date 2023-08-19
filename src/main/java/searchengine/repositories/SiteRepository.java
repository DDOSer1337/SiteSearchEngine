package searchengine.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Enum.SiteStatus;
import searchengine.model.Site;

@Repository
public interface SiteRepository extends CrudRepository<Site,Long> {
    @Query(value ="Select * FROM skillbox.sites WHERE name = :name",nativeQuery = true)
    Site getSiteByName(@Param("name")String name);
    @Query(value ="SELECT CASE WHEN frequency > 0 THEN true ELSE false END FROM skillbox.sites WHERE name = :name",nativeQuery = true)
    boolean isExist(@Param("name") String name);
    @Modifying
    @Query(value ="DELETE FROM skillbox.sites WHERE name = :name",nativeQuery = true)
    void deleteByName(@Param("name") String name);
    @Modifying
    @Query(value ="Update skillbox.sites Set site_status = %:status%  WHERE name = :name",nativeQuery = true)
    void UpdateErrorByName(@Param("name") String name, @Param("status")SiteStatus siteStatus);
}
