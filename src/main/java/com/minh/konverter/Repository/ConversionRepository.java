package com.minh.konverter.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.minh.konverter.Model.Conversion;
import com.minh.konverter.Model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversionRepository extends JpaRepository<Conversion, Long> {
    List<Conversion> findByUserOrderByConversionDateDesc(User user);
    Optional<Conversion> findByConversionIdAndUser(Long conversionId, User user);
    
    List<Conversion> findByUserAndConversionDateBetween(
        User user, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
}