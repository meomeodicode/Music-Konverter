import React, { useState, useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import axios from "axios";

function Playlists() {
  const [playlists, setPlaylists] = useState([]);
  const location = useLocation();

  useEffect(() => {
    const fetchPlaylists = async () => {
      try {
        const params = new URLSearchParams(location.search);
        const token = params.get("access_token");
        if (token) {
          localStorage.setItem("access_token", token);
        }
        const storedToken = localStorage.getItem("access_token");
        console.log("Access Token:", storedToken);
        if (!storedToken) {
          console.error("No access token available");
          return;
        }
        const response = await axios.get("http://localhost:8080/playlists", {
          headers: {
            Authorization: `Bearer ${storedToken}`,
          },
        });
        const validPlaylists = response.data.filter(
          (playlist) => playlist && playlist.id
        );
        setPlaylists(validPlaylists);
      } catch (error) {
        console.error("Error fetching playlists:", error);
      }
    };
    fetchPlaylists();
  }, [location]);

  return (
    <div>
      <h1>My Playlists</h1>
      <ul>
        {playlists.length > 0 ? (
          playlists.map((playlist) => (
            <li key={playlist.id}>
              <Link to={`/playlist/${playlist.id}`}>{playlist.name}</Link>
            </li>
          ))
        ) : (
          <p>No playlists available</p>
        )}
      </ul>
    </div>
  );
}

export default Playlists;
