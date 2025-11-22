package com.drinkspeed.repository;

import com.drinkspeed.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByRoomCodeOrderByTotalSojuEquivalentDesc(String roomCode);

    @Query("SELECT COUNT(u) FROM User u WHERE u.roomCode = :roomCode AND u.finishedAt IS NULL")
    long countActiveUsersByRoomCode(@Param("roomCode") String roomCode);

    List<User> findByRoomCode(String roomCode);

    @Query("SELECT u FROM User u WHERE u.roomCode = :roomCode AND u.finishedAt IS NULL")
    List<User> findActiveUsersByRoomCode(@Param("roomCode") String roomCode);
}