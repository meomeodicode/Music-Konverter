import React, { useState, useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import axios from "axios";

function Playlists() {
  const [playlists, setPlaylists] = useState([]);
  const [tracks, setTracks] = useState([]);
  const location = useLocation();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get("access_token");
    const service = new URLSearchParams(document.location.search).get("service");

    if (token) {
      localStorage.setItem(`${service}_token`, token);
    }

    fetchPlaylists();
  }, [location]);

  const fetchPlaylists = async () => {
    try {
      const service = new URLSearchParams(document.location.search).get("service");
      const token = localStorage.getItem(`${service}_token`);

      if (!token) return;

      const endpoint = service === 'spotify' ? '/spotify/playlists' : '/youtube/playlists';
      const response = await axios.get(`http://localhost:8080${endpoint}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setPlaylists(response.data.filter(playlist => playlist?.id));
    } catch (error) {
      console.error("Error fetching playlists:", error);
    }
  };

  const handlePlaylistClick = async (playlistId) => {
    try {
      const service = new URLSearchParams(document.location.search).get("service");
      const token = localStorage.getItem(`${service}_token`);

      if (!token) return;

      const endpoint = service === 'spotify'
        ? `/spotify/playlists/${playlistId}/tracks`
        : `/youtube/playlists/${playlistId}/tracks`;

      const response = await axios.get(`http://localhost:8080${endpoint}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setTracks(response.data);
    } catch (error) {
      console.error("Error fetching tracks:", error);
    }
  };

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">My Playlists</h1>
      <ul className="space-y-2">
        {playlists.map((playlist, index) => (
          <li key={`playlist-${playlist.id || index}`}>
            <button
              onClick={() => handlePlaylistClick(playlist.id)}
              className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
            >
              {playlist.name || `Playlist ${index + 1}`}
            </button>
          </li>
        ))}
      </ul>

      {tracks.length > 0 && (
        <div className="mt-8">
          <h2 className="text-xl font-bold mb-4">Tracks</h2>
          <ul className="space-y-2">
            {tracks.map((track, index) => (
              <li
                key={`track-${track.id || index}`}
                className="p-2 bg-gray-100 rounded"
              >
                {track.name || `Track ${index + 1}`}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

export default Playlists;

