package searchengine.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Index;

import java.util.List;

@Repository
public interface IndexRepository extends CrudRepository<Index,Long> {
    @Query(value ="SELECT CASE WHEN count(*) >0 THEN 'true' ELSE 'false' END as `boolean` FROM skillbox.indices WHERE pages_id = :pageID and lemmas_id = :lemmaID",nativeQuery = true)
    boolean isExist(@Param("pageID") int pageID, @Param("lemmaID") int lemmasID);

    @Query(value ="Select * from skillbox.indices where pages_id = :pageID and lemmas_id = :lemmaID",nativeQuery = true)
    List<Index> findIndexes(@Param("pageID") int pageID, @Param("lemmaID") int lemmasID);

    @Query(value ="Select `rank` from skillbox.indices where pages_id = :pageID and lemmas_id = :lemmaID",nativeQuery = true)
    Integer getRank(@Param("pageID") int pageID, @Param("lemmaID") int lemmasID);

    @Query(value ="UPDATE skillbox.indices SET `rank` = `rank` + 1 where pages_id = :pageID and lemmas_id = :lemmaID",nativeQuery = true)
    void updateRank(@Param("pageID") int pageID, @Param("lemmaID") int lemmasID);
}
