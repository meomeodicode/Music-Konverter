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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @Column(name = "sourcepf", nullable = false)
    private String sourcePlatform;

    @Column(name = "targetpf", nullable = false)
    private String targetPlatform;

    @Column(name = "playlistid", nullable = false)
    private String playlistId;

    @Column(name = "conversiondate", nullable = false)
    private LocalDateTime conversionDate;
}