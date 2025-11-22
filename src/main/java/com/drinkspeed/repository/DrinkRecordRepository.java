package com.drinkspeed.repository;

import com.drinkspeed.domain.DrinkRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrinkRecordRepository extends JpaRepository<DrinkRecord, Long> {

    List<DrinkRecord> findByUserIdOrderByRecordedAtAsc(Long userId);

    List<DrinkRecord> findByUserId(Long userId);
}
