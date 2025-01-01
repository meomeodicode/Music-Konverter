import { CiSquareQuestion } from "react-icons/ci";

import { useNavigate, useLocation } from "react-router-dom";
import Navbar from "../navbar/Navbar";
import Footer from "../footer/Footer";

const HomepageAfter = () => {
    const navigate = useNavigate(); // React Router's hook for programmatic navigation
    const location = useLocation(); // Hook to access current URL

    const handleNavigateWithQuery = (path) => {
        const searchParams = new URLSearchParams(location.search); // Preserve current query params
        navigate(`${path}?${searchParams.toString()}`); // Append them to the new path
    };

    return (
        <div className="bg-gray-900 text-white min-h-screen min-w-fit">
            {/* Navigation Bar */}
            <Navbar />

            {/* Hero Section */}
            <section className="grid grid-cols-12 gap-6 items-center justify-center mt-36 lg:px-12 mb-64">
                {/* Left Section - Text */}
                <div className="col-span-12 sm:col-span-5 text-left">
                    <h1 className="text-4xl lg:text-5xl font-bold mb-6">
                        TRANSFER YOUR PLAYLISTS SEAMLESSLY
                    </h1>
                    <p className="text-lg text-gray-300">
                        The most reliable and fast solution to recreate your music collection across music services.
                    </p>
                </div>

                {/* Right Section - Buttons (2x2 Grid) */}
                <div className="col-span-12 sm:col-span-7 grid grid-cols-1 sm:grid-cols-2 gap-6">
                    <button
                        className="w-full px-12 py-12 bg-gradient-to-tr from-yellow-400 hover:bg-yellow-400 text-gray-900 text-3xl font-bold rounded-lg"
                        onClick={() => handleNavigateWithQuery("/playlists")}
                    >
                        Transfer
                    </button>
                    <button
                        className="w-full px-12 py-12 bg-gradient-to-tr from-yellow-400 hover:bg-yellow-400 text-gray-900 text-3xl font-bold rounded-lg"

                    >
                        Syncs
                    </button>
                    <button
                        className="w-full px-12 py-12 bg-gradient-to-tr from-yellow-400 hover:bg-yellow-400 text-gray-900 text-3xl font-bold rounded-lg"
                        onClick={() => handleNavigateWithQuery("/identify-songs")}
                    >
                        Identify Songs
                    </button>
                    <button
                        className="w-full px-12 py-12 bg-gradient-to-tr from-yellow-400 hover:bg-yellow-400 text-gray-900 text-3xl font-bold rounded-lg"

                    >
                        Platform Links
                    </button>
                </div>
            </section>

            {/* Footer Section */}
            <Footer />

            {/* Help Section */}
            <div className="fixed bottom-8 right-8 text-5xl text-yellow-400 cursor-pointer hover:text-yellow-600">
                <CiSquareQuestion />
            </div>
        </div>
    );
}

export default HomepageAfter