package searchengine.repositories;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Enum.SiteStatus;
import searchengine.model.Site;

@Repository
public interface SiteRepository extends CrudRepository<Site,Long> {
    @Query(value ="Select * FROM skillbox.sites WHERE name like :name",nativeQuery = true)
    Site getSiteByName(@Param("name")String name);
    @Query(value ="SELECT CASE WHEN count(*) >0 THEN 'true' ELSE 'false' END as `boolean` FROM skillbox.sites where name = :name",nativeQuery = true)
    boolean isExist(@Param("name") String name);
    @Transactional
    @Modifying
    @Query(value ="DELETE FROM skillbox.sites WHERE name = ':name'",nativeQuery = true)
    void deleteByName(@Param("name") String name);
    @Transactional
    @Modifying
    @Query(value ="Update skillbox.sites Set site_status = :status WHERE name = :name",nativeQuery = true)
    void UpdateErrorByName(@Param("name") String name, @Param("status")SiteStatus siteStatus);
}
