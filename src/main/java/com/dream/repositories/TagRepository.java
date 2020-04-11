package com.dream.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dream.models.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
	@Query(value = "select t.* from tag t join dream_tag dt on t.id = dt.tag_id where dt.dream_id = :dreamId", nativeQuery = true)
	Set<Tag> findByDreamId(@Param("dreamId") long dreamId);
	
}
