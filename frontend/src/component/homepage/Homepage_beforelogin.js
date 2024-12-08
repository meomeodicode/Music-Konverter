import { AiFillYoutube } from "react-icons/ai";
import { AiFillSpotify } from "react-icons/ai";
import { CiSquareQuestion } from "react-icons/ci";
import { useNavigate } from "react-router-dom";

const HomepageBefore = () => {
    const navigate = useNavigate(); // React Router's hook for programmatic navigation

    const goToRegister = () => navigate("/register"); // Navigate to the Register page
    const goToLogin = () => navigate("/login"); // Navigate to the Login page

    return (
        <div className="bg-gray-900 text-white min-h-screen min-w-fit">
            {/* Navigation Bar */}
            <nav className="flex justify-between items-center p-8 sticky bg-gray-900 top-0 z-50">
                <div className="text-4xl font-bold px-4 font-orbitron cursor-pointer bg-gradient-to-tr from-yellow-300 text-transparent bg-clip-text hover:bg-yellow-400">Movesic</div>
                <div className="flex items-center space-x-12 px-4">
                    <ul className="flex space-x-12 text-xl">
                        <li onClick={goToRegister} className="hover:text-yellow-400 cursor-pointer">Transfer</li>
                        <li onClick={goToRegister} className="hover:text-yellow-400 cursor-pointer">Syncs</li>
                        <li onClick={goToRegister} className="hover:text-yellow-400 cursor-pointer">Identify songs</li>
                        <li onClick={goToRegister} className="hover:text-yellow-400 cursor-pointer">Platforms link</li>
                        <li onClick={goToLogin} className="hover:text-yellow-400 cursor-pointer">Log in</li>
                    </ul>
                    <button onClick={goToRegister} className="bg-gradient-to-tr from-yellow-400 hover:bg-yellow-400 text-gray-900 text-xl px-4 py-2 rounded-lg font-bold">
                        Get started now
                    </button>
                </div>
            </nav>

            {/* Hero Section */}
            <section className="flex flex-col items-center text-center mt-28 px-4">
                <h1 className="text-4xl lg:text-5xl font-bold mb-4">
                    TRANSFER YOUR PLAYLISTS SEAMLESSLY
                </h1>
                <p className="text-lg text-gray-300 mb-8 max-w-xl">
                    The most reliable and fast solution to recreate your music collection across music services.
                </p>
                <button onClick={goToRegister} className="bg-gradient-to-tr from-yellow-400 hover:bg-yellow-400 text-gray-900 px-6 py-3 rounded-lg text-lg font-bold">
                    Let's Start
                </button>
            </section>

            {/* Icon Section */}
            <div className="flex justify-center items-center mt-16 space-x-24">
                {/* Spotify Logo */}
                <AiFillSpotify className='text-8xl' />

                {/* YouTube Logo */}
                <AiFillYoutube className='text-8xl' />
            </div>

            {/* Help Section */}
            <div className="fixed bottom-8 right-8 text-5xl text-yellow-400 cursor-pointer hover:text-yellow-600">
                <CiSquareQuestion />
            </div>

            {/* Introduction Section */}
            <div>
                <section className="flex flex-col items-center mt-64">
                    <h1 className="text-4xl lg:text-5xl font-bold mb-4">
                        WHAT IS MOVESIC
                    </h1>
                    <p className="text-xl text-justify text-gray-300 mt-12 mb-64 max-w-3xl">
                        Movesic is a powerful system that allows users to transfer music playlists between streaming platforms effortlessly. Whether it's moving a Spotify playlist to YouTube Music or identifying background music from a video, Movesic offers a suite of tools to streamline the process. The platform is aimed at a wide variety of users, from casual music lovers to long-time subscribers looking for more control over their music libraries. Movesic is designed to compete with similar tools by offering a unique blend of features not available in one package elsewhere.
                    </p>
                </section>
            </div>
        </div>
    );
}

export default HomepageBefore