import React from "react";
import thumbnail from "./resources/music.jpg";

const Home = () => {
  const handleAuthorization = () => {
    window.location.href = "http://localhost:8080/login";
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
          <button className="btn" onClick={handleAuthorization}>
            Authorize Spotify
          </button>
          <img
            className="landingPageImage"
            src={thumbnail}
            alt="Spotify to Youtube Music and vice versa"
          />
        </div>
      </div>
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
