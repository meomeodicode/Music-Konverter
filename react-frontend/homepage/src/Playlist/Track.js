import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import PlaylistCard from "./Playlist";
import axios from "axios";

function PlaylistSongs() {
  const { playlistId } = useParams();
  const [songs, setSongs] = useState([]);
  const [playlistInfo, setPlaylistInfo] = useState(null);

  useEffect(() => {
    const fetchPlaylistData = async () => {
      try {
        const token = localStorage.getItem("access_token");
        if (!token) {
          console.error("No access token available");
          return;
        }

        // Fetch playlist tracks
        const tracksResponse = await axios.get(
          `http://localhost:8080/playlists/${playlistId}/tracks`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        setSongs(tracksResponse.data);

        // Optionally, fetch playlist info (if needed for PlaylistCard)
        const playlistResponse = await axios.get(
          `http://localhost:8080/playlists/${playlistId}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        setPlaylistInfo(playlistResponse.data);
      } catch (error) {
        console.error("Error fetching playlist data:", error);
      }
    };

    fetchPlaylistData();
  }, [playlistId]);

  const calculateDuration = (tracks) => {
    const totalMs = tracks.reduce(
      (acc, track) => acc + (track.durationMs || 0),
      0
    );
    const minutes = Math.floor(totalMs / 60000);
    const seconds = Math.floor((totalMs % 60000) / 1000);
    return `${minutes} min ${seconds} sec`;
  };

  const toYoutube = async () => {
    try {
      const spotifyAccessToken = localStorage.getItem("access_token");
      const youtubeAccessToken = localStorage.getItem("youtube_access_token"); // Make sure this token is available
      if (!spotifyAccessToken || !youtubeAccessToken) {
        alert("Access tokens for Spotify or YouTube are missing. Please log in.");
        return;
      }
  
      // Call the backend API to transfer playlist to YouTube and save the conversion
      const response = await axios.post("http://localhost:8080/transfer-to-youtube", {
        spotifyAccessToken, // Spotify access token
        youtubeAccessToken, // YouTube access token
        spotifyPlaylistId: playlistId, // Playlist ID from Spotify
        playlistName: playlistInfo?.name, // Playlist name (optional)
      });
  
      // If successful, show success message
      alert(`Successfully transferred playlist "${playlistInfo?.name}" to YouTube.`);
    } catch (error) {
      console.error("Error transferring playlist to YouTube:", error);
      alert("Failed to transfer playlist. Please try again.");
    }
  };
  
  return (
    <div>
      {playlistInfo && (
        <PlaylistCard
          uid={playlistInfo.id}
          name={playlistInfo.name}
          image={
            playlistInfo.images && playlistInfo.images.length > 0
              ? playlistInfo.images[0].url
              : ""
          }
          no_of_songs={songs.length}
          duration={calculateDuration(songs)}
          isSelected={false}
          handleCheckboxChange={() => {}}
        />
      )}

      <h1>Songs in Playlist</h1>

      {/* Fixed the button syntax and added content */}
      <button className="ytBTN" onClick={toYoutube}>
        Transfer to YouTube
      </button>

      <ul>
        {songs.length > 0 ? (
          songs.map((song) => (
            <li key={song.id}>
              {song.trackName} - {song.artistNames.join(", ")}
            </li>
          ))
        ) : (
          <p>No songs available</p>
        )}
      </ul>
    </div>
  );
}

export default PlaylistSongs;
