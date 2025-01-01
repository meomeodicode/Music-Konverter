import UserDropdown from "../userdropdown/UserDropdown";
import { useNavigate, useLocation } from "react-router-dom";

const Navbar = () => {
    const navigate = useNavigate(); // React Router's hook for programmatic navigation
    const location = useLocation(); // Hook to access current URL

    const handleNavigateWithQuery = (path) => {
        const searchParams = new URLSearchParams(location.search); // Preserve current query params
        navigate(`${path}?${searchParams.toString()}`); // Append them to the new path
    };

    return (
        <nav className="flex justify-between items-center p-8 sticky bg-gray-900 top-0 z-50">
            <div
                onClick={() => handleNavigateWithQuery("/home")}
                className="text-4xl font-bold px-4 font-orbitron cursor-pointer bg-gradient-to-tr from-yellow-300 text-transparent bg-clip-text hover:bg-yellow-400"
            >
                Movesic
            </div>
            <div className="flex items-center space-x-12 px-4">
                <ul className="flex space-x-12 text-xl">
                    <li
                        onClick={() => handleNavigateWithQuery("/playlists")}
                        className="hover:text-yellow-400 cursor-pointer"
                    >
                        Transfer
                    </li>
                    <li className="hover:text-yellow-400 cursor-pointer">Syncs</li>
                    <li
                        onClick={() => handleNavigateWithQuery("/identify-songs")}
                        className="hover:text-yellow-400 cursor-pointer"
                    >
                        Identify songs
                    </li>
                    <li className="hover:text-yellow-400 cursor-pointer">Platforms link</li>
                </ul>
                <UserDropdown />
            </div>
        </nav>
    );
};

export default Navbar;
