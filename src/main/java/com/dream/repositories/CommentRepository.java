package com.dream.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dream.models.Comment;
import com.dream.models.CommentNumbers;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndApprovedTrue(Long id);

    Page<Comment> findByDreamIdAndApprovedTrue(Long dreamId, Pageable pageable);
    
    @Transactional
    @Query(value = "update comment set likes_no = likes_no + 1 where id = :id", nativeQuery = true)
    @Modifying
    void addLikeToComment(@Param("id") Long id);

    @Transactional
    @Query(value = "update comment set dislikes_no = dislikes_no + 1 where id = :id", nativeQuery = true)
    @Modifying
    void addDislikeToComment(@Param("id") Long id);

    @Transactional
    @Query(value = "update comment set likes_no = likes_no - 1 where id = :id", nativeQuery = true)
    @Modifying
    void removeLikeFromComment(@Param("id") Long id);

    @Transactional
    @Query(value = "update comment set dislikes_no = dislikes_no - 1 where id = :id", nativeQuery = true)
    @Modifying
    void removeDislikeFromComment(@Param("id") Long id);

    @Transactional
    @Query(value = "update comment set approved=1 where id = :id", nativeQuery = true)
    @Modifying
    void approveComment(@Param("id") Long id);

    List<Comment> findByApprovedFalse();

    Page<Comment> findByParentIdAndApprovedTrue(Long parentId, Pageable pageable);
    
    
    @Query(value = "select likes_no, dislikes_no \n" + 
			   	   "from comment \n" + 
			   	   "where id=:id and approved = 1", 
		nativeQuery = true)
    CommentNumbers getCommentNumbers(@Param("id") Long id);
}
