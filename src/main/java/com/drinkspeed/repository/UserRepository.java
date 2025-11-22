package com.drinkspeed.repository;

import com.drinkspeed.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByRoomIdOrderByTotalSojuEquivalentDesc(Long roomId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.room.id = :roomId AND u.finishedAt IS NULL")
    long countActiveUsersByRoomId(@Param("roomId") Long roomId);

    List<User> findByRoomId(Long roomId);

    @Query("SELECT u FROM User u WHERE u.room.id = :roomId AND u.finishedAt IS NULL")
    List<User> findActiveUsersByRoomId(@Param("roomId") Long roomId);
}
