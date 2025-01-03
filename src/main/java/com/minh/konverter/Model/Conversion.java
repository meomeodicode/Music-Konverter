package com.minh.konverter.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversions")
public class Conversion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversionid")
    private Long conversionId;

    @Column(name = "uid", nullable = false)
    private Long userId;

    @Column(name = "sourcepf", nullable = false)
    private String sourcePlatform;

    @Column(name = "targetpf", nullable = false)
    private String targetPlatform;

    @Column(name = "playlistid", nullable = false)
    private String playlistId;

    @Column(name = "conversiondate", nullable = false)
    private LocalDateTime conversionDate;

    public Conversion() {
    }

    public Conversion(Long userId, String sourcePlatform, String targetPlatform, String playlistId, LocalDateTime conversionDate) {
        this.userId = userId;
        this.sourcePlatform = sourcePlatform;
        this.targetPlatform = targetPlatform;
        this.playlistId = playlistId;
        this.conversionDate = conversionDate;
    }

    public Long getConversionId() {
        return conversionId;
    }

    public void setConversionId(Long conversionId) {
        this.conversionId = conversionId;
    }

    public Long getUid() {
        return userId;
    }

    public void setUid(Long userId) {
        this.userId = userId;
    }

    public String getSourcePlatform() {
        return sourcePlatform;
    }

    public void setSourcePlatform(String sourcePlatform) {
        this.sourcePlatform = sourcePlatform;
    }

    public String getTargetPlatform() {
        return targetPlatform;
    }

    public void setTargetPlatform(String targetPlatform) {
        this.targetPlatform = targetPlatform;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public LocalDateTime getConversionDate() {
        return conversionDate;
    }

    public void setConversionDate(LocalDateTime conversionDate) {
        this.conversionDate = conversionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversion that = (Conversion) o;
        return conversionId != null && conversionId.equals(that.conversionId);
    }

    @Override
    public int hashCode() {
        return conversionId != null ? conversionId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Conversion{" +
                "conversionId=" + conversionId +
                ", userId=" + userId +
                ", sourcePlatform='" + sourcePlatform + '\'' +
                ", targetPlatform='" + targetPlatform + '\'' +
                ", playlistId='" + playlistId + '\'' +
                ", conversionDate=" + conversionDate +
                '}';
    }
}
