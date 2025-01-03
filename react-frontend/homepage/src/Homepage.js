import React, { useState } from "react";
import thumbnail from "./resources/music.jpg";
import dayjs from "dayjs";

const Home = () => {
  const [conversions, setConversions] = useState([]); // Store fetched conversions
  const [showHistory, setShowHistory] = useState(false); // Toggle history visibility

  // Function to fetch conversions from the backend
  const fetchConversions = async () => {
    try {
      const userId = 4; // Hardcoded for testing purposes
      const response = await fetch(`http://localhost:8080/api/conversion-history?userId=${userId}`);
      if (!response.ok) {
        throw new Error("Failed to fetch conversion history");
      }
      const data = await response.json();
      setConversions(data); // Update conversions state
      setShowHistory(true); // Show history section
    } catch (error) {
      console.error("Error fetching conversion history:", error);
      alert("Failed to fetch history. Please try again later.");
    }
  };

  return (
    <section className="landing-page">
      <div className="main-container">
        <div className="landing-inner-div">
          <h1>Transfer playlists from Spotify to Youtube Music.</h1>
          <p>
            <strong>
              Ensure you have accounts with both Spotify and Youtube Music
              before starting.
            </strong>
          </p>
          <button className="btn" onClick={() => window.location.href = "http://localhost:8080/login"}>
            Authorize Spotify
          </button>
          <button className="btn" onClick={fetchConversions}>
            History
          </button>
          <img
            className="landingPageImage"
            src={thumbnail}
            alt="Spotify to Youtube Music and vice versa"
          />
        </div>
      </div>

      {/* Display history if available */}
      {showHistory && (
        <div className="history-container">
          <h2>Conversion History</h2>
          {conversions.length > 0 ? (
            <ul className="history-list">
              {conversions.map((conversion) => (
                <li key={conversion.conversionId} className="history-item">
                  <p><strong>Source Platform:</strong> {conversion.sourcePlatform || "N/A"}</p>
                  <p><strong>Target Platform:</strong> {conversion.targetPlatform || "N/A"}</p>
                  <p><strong>Playlist ID:</strong> {conversion.playlistId || "N/A"}</p>
                  <p><strong>Conversion Date:</strong> {conversion.conversionDate ? dayjs(conversion.conversionDate).format("YYYY-MM-DD HH:mm:ss") : "N/A"}</p>
                </li>
              ))}
            </ul>
          ) : (
            <p>No conversion history found.</p>
          )}
        </div>
      )}

      <footer>
        <div className="footer-container">
          <p> Created by a music nerd </p>
          <p> Buy me a coffee </p>
        </div>
      </footer>
    </section>
  );
};

export default Home;
