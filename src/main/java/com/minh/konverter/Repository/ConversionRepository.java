package com.minh.konverter.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.minh.konverter.Model.Conversion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversionRepository extends JpaRepository<Conversion, Long> {
    
    // Find all conversions for a specific user by userId, ordered by conversionDate (descending)
    List<Conversion> findByUserIdOrderByConversionDateDesc(Long userId);

    // Find a specific conversion by conversionId and userId
    Optional<Conversion> findByConversionIdAndUserId(Long conversionId, Long userId);

    // Find all conversions for a specific user within a date range
    List<Conversion> findByUserIdAndConversionDateBetween(
        Long userId, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
}