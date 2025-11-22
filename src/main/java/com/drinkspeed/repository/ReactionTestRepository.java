package com.drinkspeed.repository;

import com.drinkspeed.domain.ReactionTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactionTestRepository extends JpaRepository<ReactionTest, Long> {

    List<ReactionTest> findByUserIdOrderByTestedAtAsc(Long userId);

    @Query("SELECT AVG(rt.reactionTimeMs) FROM ReactionTest rt WHERE rt.user.id = :userId")
    Double findAvgReactionTimeByUserId(@Param("userId") Long userId);

    List<ReactionTest> findByUserId(Long userId);
}
