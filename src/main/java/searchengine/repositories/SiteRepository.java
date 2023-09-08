package searchengine.repositories;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Enum.SiteStatus;
import searchengine.model.Site;

import java.util.Optional;

@Repository
public interface SiteRepository extends CrudRepository<Site,Long> {

    Site findByName(String name);
    Site findByUrl(String url);

    boolean existsByName(String name);

    @Transactional
    @Modifying
    void deleteByName(String name);

    @Transactional
    @Modifying
    @Query(value ="Update skillbox.sites Set site_status = :status WHERE name = :name",nativeQuery = true)
    void UpdateErrorByName(@Param("name") String name, @Param("status")SiteStatus siteStatus);
}
