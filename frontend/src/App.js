import './App.css';
import HomepageBefore from './component/homepage/Homepage_beforelogin';
import HomepageAfter from './component/homepage/Homepage_afterlogin';
import Register from './component/register/Register';
import Login from './component/login/Login';
import Transfer from './component/transfer/Transfer';
import IdentifySong from './component/identifysong/IdentifySong';
import TransferPlaylists from './component/transferPlaylists/TransferPlaylist';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";


function App() {
  return (
    <Router>
      <Routes>
        {/* Define routes for different pages */}
        <Route path="/" element={<HomepageBefore />} />
        <Route path="/home" element={<HomepageAfter />} />
        <Route path="/register" element={<Register />} />
        <Route path="/signin" element={<Login />} />
        <Route path="/transfer" element={<Transfer />} />
        <Route path="/identify-songs" element={<IdentifySong />} />
        <Route path="/playlists" element={<TransferPlaylists />} />
      </Routes>
    </Router>
  )
}

export default App;
