package com.dream.repositories;

import com.dream.models.Dream;
import com.dream.models.DreamNumbers;
import com.dream.models.DreamProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DreamRepository extends JpaRepository<Dream, Long> {

    @Transactional
    @Query(value = "update dream set likes_no = likes_no + 1 where id = :id", nativeQuery = true)
    @Modifying
    void addLikeToDream(@Param("id") Long id);

    @Transactional
    @Query(value = "update dream set dislikes_no = dislikes_no + 1 where id = :id", nativeQuery = true)
    @Modifying
    void addDislikeToDream(@Param("id") Long id);

    @Transactional
    @Query(value = "update dream set likes_no = likes_no - 1 where id = :id", nativeQuery = true)
    @Modifying
    void removeLikeFromDream(@Param("id") Long id);

    @Transactional
    @Query(value = "update dream set dislikes_no = dislikes_no - 1 where id = :id", nativeQuery = true)
    @Modifying
    void removeDislikeFromDream(@Param("id") Long id);

    @Transactional
    @Query(value = "update dream set same_dream_no = same_dream_no + 1 where id = :id", nativeQuery = true)
    @Modifying
    void addSameDreamToDream(@Param("id") Long id);

    Dream findFirstByApprovedTrueOrderByLikesNoDesc();

    Dream findFirstByApprovedTrueOrderByDislikesNoDesc();

    Dream findFirstByApprovedTrueOrderBySameDreamNoDesc();

    @Transactional
    @Query(value = "update dream set approved=1 where id = :id", nativeQuery = true)
    @Modifying
    void approveDream(@Param("id") Long id);

    Page<Dream> findByTags_Id(Long id, Pageable pageable);
    
    Page<Dream> findByApproved(boolean approved, Pageable pageable);

    @Query(value = "select \n" + 
						"d.*, " +
						"count(distinct(case when c.dream_id = d.id and c.approved = 1 then c.id end)) as commentCount\n" + 
					"from dream d left join dream_tag dt on d.id=dt.dream_id left join comment c on d.id = c.dream_id\n" + 
					"where d.approved = 1\n" + 
					"and (:keyword is null or (d.dream_description like %:keyword% or d.id like %:keyword%))\n"+
					"and (:tagId is null or dt.tag_id = :tagId) \n" +
					"group by d.id, d.approved\n" + 
					"order by commentCount desc", 
			countProjection = "*", 
			nativeQuery = true)
    Page<DreamProjection> findAllOrderByCommentCountDesc(@Param("keyword") String keyword, @Param("tagId") Long tagId, Pageable pageable);

    @Query(value = "select \n" + 
						"d.*, " +
						"count(distinct(case when c.dream_id = d.id and c.approved = 1 then c.id end)) as commentCount\n" + 
					"from dream d left join dream_tag dt on d.id=dt.dream_id left join comment c on d.id = c.dream_id\n" + 
					"where d.approved = 1\n" + 
					"and d.create_date >= :createDate\n" +
					"and (:keyword is null or (d.dream_description like %:keyword% or d.id like %:keyword%))\n"+
					"and (:tagId is null or dt.tag_id = :tagId) \n" +
					"group by d.id, d.approved\n" +
					"order by likes_no desc", 
			countProjection = "*", 
			nativeQuery = true)
    Page<DreamProjection> findByCreateDateGreaterThanEqualOrderByLikesNoDesc(@Param("keyword") String keyword, @Param("tagId") Long tagId, @Param("createDate") LocalDateTime createDate, Pageable pageable);
    
    @Query(value = "select \n" + 
						"d.*, " +
						"count(distinct(case when c.dream_id = d.id and c.approved = 1 then c.id end)) as commentCount\n" + 
					"from dream d left join dream_tag dt on d.id=dt.dream_id left join comment c on d.id = c.dream_id\n" + 
					"where d.approved = 1\n" + 
					"and (:keyword is null or (d.dream_description like %:keyword% or d.id like %:keyword%))\n"+
					"and (:tagId is null or dt.tag_id = :tagId) \n" +
					"group by d.id, d.approved\n", 
			countProjection = "*", 
			nativeQuery = true)
    Page<DreamProjection> findAll(@Param("keyword") String keyword, @Param("tagId") Long tagId, Pageable pageable);
    
    @Query(value = "select \n" + 
						"d.*, " +
						"count(distinct(case when c.dream_id = d.id and c.approved = 1 then c.id end)) as commentCount\n" + 
					"from dream d left join dream_tag dt on d.id=dt.dream_id left join comment c on d.id = c.dream_id\n" + 
					"where d.id = :id\n" +
					"and d.approved = 1\n" +
					"group by d.id, d.approved\n" + 
					"limit 1", 
			nativeQuery = true)
	Optional<DreamProjection> getDreamProjectionById(@Param("id") Long id);
    
    @Query(value = "select \n" + 
						"d.*, " +
						"count(distinct(case when c.dream_id = d.id and c.approved = 1 then c.id end)) as commentCount\n" + 
					"from dream d left join dream_tag dt on d.id=dt.dream_id left join comment c on d.id = c.dream_id\n" + 
					"and d.approved = 1\n" +
					"group by d.id, d.approved\n" +
					"order by d.create_date desc\n" +
					"limit :limit,1", 
			nativeQuery = true)
	Optional<DreamProjection> getRandomDream(@Param("limit") Integer limit);
    
    @Query(value = "select \n" + 
						"d.*, " +
						"count(distinct(case when c.dream_id = d.id and c.approved = 1 then c.id end)) as commentCount\n" + 
					"from dream d left join dream_tag dt on d.id=dt.dream_id left join comment c on d.id = c.dream_id\n" + 
					"and d.approved = 1\n" +
					"group by d.id, d.approved\n" +
					"order by d.likes_no desc\n" +
					"limit 1", 
			nativeQuery = true)
	Optional<DreamProjection> getMostLikedDream();
    
    @Query(value = "select \n" + 
						"d.*, " +
						"count(distinct(case when c.dream_id = d.id and c.approved = 1 then c.id end)) as commentCount\n" + 
					"from dream d left join dream_tag dt on d.id=dt.dream_id left join comment c on d.id = c.dream_id\n" + 
					"and d.approved = 1\n" +
					"group by d.id, d.approved\n" +
					"order by d.dislikes_no desc\n" +
					"limit 1", 
			nativeQuery = true)
    Optional<DreamProjection> getMostDislikedDream();
    
    @Query(value = "select \n" + 
						"d.*, " +
						"count(distinct(case when c.dream_id = d.id and c.approved = 1 then c.id end)) as commentCount\n" + 
					"from dream d left join dream_tag dt on d.id=dt.dream_id left join comment c on d.id = c.dream_id\n" + 
					"and d.approved = 1\n" +
					"group by d.id, d.approved\n" +
					"order by d.same_dream_no desc\n" +
					"limit 1", 
			nativeQuery = true)
    Optional<DreamProjection> getMostSameDream();
    
    @Query(value = "select likes_no, dislikes_no, same_dream_no \n" + 
				   "from dream \n" + 
				   "where id=:id and approved = 1", 
			nativeQuery = true)
	DreamNumbers getDreamNumbers(@Param("id") Long dreamId);
}
