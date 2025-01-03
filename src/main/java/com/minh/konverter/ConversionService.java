package com.minh.konverter;

import com.minh.konverter.Model.Conversion;
import com.minh.konverter.Model.User;
import com.minh.konverter.Repository.ConversionRepository;
import com.minh.konverter.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConversionService {

    @Autowired
    private ConversionRepository conversionRepository;
    private UserRepository userRepository;

    /**
     * Save a Conversion object to the database
     * @param conversion the conversion to save
     * @return the saved Conversion object
     */
    public Conversion saveConversion(Conversion conversion) {
        try {
            return conversionRepository.save(conversion);
        } catch (Exception e) {
            // Log the error and rethrow as runtime exception
            System.err.println("Error saving conversion: " + e.getMessage());
            throw new RuntimeException("Failed to save conversion", e);
        }
    }

    /**
     * Retrieve all conversions for a specific user
     * @param userId the user ID
     * @return list of conversions
     */
    public List<Conversion> getConversionsForUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return conversionRepository.findByUserIdOrderByConversionDateDesc(userId);
    }

    /**
     * Retrieve conversions for a user within a date range
     * @param userId the user ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of conversions
     */
    public List<Conversion> getConversionsForUserInDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }

        return conversionRepository.findByUserIdAndConversionDateBetween(userId, startDate, endDate);
    }
}