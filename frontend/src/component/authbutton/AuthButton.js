import { AiFillGoogleCircle } from "react-icons/ai";
import { AiFillFacebook } from "react-icons/ai";
import { AiFillSpotify } from "react-icons/ai";

const AuthButton = () => {
    const handleSpotifyLogin = () => {
        window.location.href = "http://localhost:8080/login";
    };

    const handleYTLogin = () => {
        window.location.href = "http://localhost:8080/redirectingToYoutube";
    };

    return (
        <div className="space-y-3 mb-6">
            <button
                className="w-full flex items-center justify-center px-4 py-2 bg-blue-400 rounded-md text-white hover:bg-blue-500"
                onClick={handleYTLogin}
            >
                <AiFillGoogleCircle className="text-3xl mr-2" />
                Sign in with Google
            </button>
            <button className="w-full flex items-center justify-center px-4 py-2 bg-blue-800 rounded-md text-white hover:bg-blue-900">
                <AiFillFacebook className="text-3xl mr-2" />
                Sign in with Facebook
            </button>
            <button
                className="w-full flex items-center justify-center px-4 py-2 bg-green-500 rounded-md text-white hover:bg-green-600"
                onClick={handleSpotifyLogin}
            >
                <AiFillSpotify className="text-3xl mr-2" />
                Sign in with Spotify
            </button>
        </div>
    )
}

export default AuthButton