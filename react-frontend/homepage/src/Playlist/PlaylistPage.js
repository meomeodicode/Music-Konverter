import React, { useState, useEffect } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import axios from "axios";

function Playlists() {
  const [playlists, setPlaylists] = useState([]);
  const location = useLocation();
  const navigate = useNavigate(); // Use for redirection if needed

  useEffect(() => {
    const fetchAndSavePlaylists = async () => {
      try {
        const params = new URLSearchParams(location.search);
        const token = params.get("access_token");
        if (token) {
          localStorage.setItem("access_token", token);
        }

        const storedToken = localStorage.getItem("access_token");
        if (!storedToken) {
          console.error("No access token available");
          navigate("/"); // Redirect to home if no token
          return;
        }

        // Fetch playlists from Spotify
        const response = await axios.get("http://localhost:8080/playlists", {
          headers: {
            Authorization: `Bearer ${storedToken}`,
          },
        });

        const validPlaylists = response.data.filter(
          (playlist) => playlist && playlist.id
        );

        setPlaylists(validPlaylists);

        // Save each playlist to the database
        validPlaylists.forEach(async (playlist) => {
          try {
            await axios.post("http://localhost:8080/api/save-playlist", {
              title: playlist.name,
              platform: "Spotify",
              userId: 1, // Hardcoded userId for demo purposes
            });
            console.log(`Playlist "${playlist.name}" saved to the database.`);
          } catch (error) {
            console.error(
              `Error saving playlist "${playlist.name}" to the database:`,
              error
            );
          }
        });
      } catch (error) {
        console.error("Error fetching playlists:", error);
        alert("Failed to fetch playlists. Please try logging in again.");
        navigate("/"); // Redirect to home on error
      }
    };

    fetchAndSavePlaylists();
  }, [location, navigate]);

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
