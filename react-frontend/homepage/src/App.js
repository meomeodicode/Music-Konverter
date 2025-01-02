import React from "react";
import Home from "./Homepage";
import Playlists from "./Playlist/PlaylistPage";
import PlaylistSongs from "./Playlist/Track";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";

class App extends React.Component {
  render() {
    return (
      <main>
        <Router>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/playlists" element={<Playlists />} />
            <Route path="/:service/playlists/:playlistId/tracks" element={<PlaylistSongs />} />
          </Routes>
        </Router>
      </main>
    );
  }
}

export default App;
