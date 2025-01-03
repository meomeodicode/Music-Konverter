package com.minh.konverter.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minh.konverter.Model.Playlist;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findByUserId(Long userId);

    List<Playlist> findByPlatform(String platform);

    boolean existsByTitleAndUserId(String title, Long userId);

    boolean existsByTitleAndPlatformAndUserId(String title, String platform, Long userId);
}
