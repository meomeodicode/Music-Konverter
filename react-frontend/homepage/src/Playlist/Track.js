import React, { useState, useEffect } from "react";
import { useParams, useLocation } from "react-router-dom";
import PlaylistCard from "./Playlist";
import axios from "axios";

function PlaylistSongs() {
  const { playlistId } = useParams();
  const [songs, setSongs] = useState([]);
  const [playlistInfo, setPlaylistInfo] = useState(null);
  const location = useLocation();

  // Get service from URL or localStorage
  const getService = () => {
    const params = new URLSearchParams(location.search);
    return params.get("service") || localStorage.getItem("current_service");
  };

  useEffect(() => {
    const fetchPlaylistData = async () => {
      try {
        const service = getService();
        const token = localStorage.getItem(`${service}_token`);
        if (!token) {
          console.error("No access token available");
          return;
        }

        // Adjust endpoints based on service
        const tracksEndpoint = service === 'spotify'
          ? `/playlists/${playlistId}/tracks`
          : `/youtube/${playlistId}/tracks`;

        const playlistEndpoint = service === 'spotify'
          ? `/playlists/${playlistId}`
          : `/youtube/${playlistId}`;

        // Fetch playlist tracks
        const tracksResponse = await axios.get(
          `http://localhost:8080${tracksEndpoint}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        setSongs(tracksResponse.data);
      } catch (error) {
        console.error("Error fetching playlist data:", error);
      }
    };

    fetchPlaylistData();
  }, [playlistId, location]);

  const calculateDuration = (tracks) => {
    const totalMs = tracks.reduce(
      (acc, track) => acc + (track.durationMs || 0),
      0
    );
    const minutes = Math.floor(totalMs / 60000);
    const seconds = Math.floor((totalMs % 60000) / 1000);
    return `${minutes} min ${seconds} sec`;
  };

  const toYoutube = () => {
    window.location.href = "http://localhost:8080/redirectingToYoutube";
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
          handleCheckboxChange={() => { }}
        />
      )}

      <h1>Songs in Playlist</h1>

      {getService() === 'spotify' && (
        <button className="ytBTN" onClick={toYoutube}>
          Transfer to YouTube
        </button>
      )}

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


