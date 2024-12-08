import { useNavigate } from "react-router-dom";
import AuthButton from "../authbutton/AuthButton";

const Login = () => {
    const navigate = useNavigate(); // React Router's hook for programmatic navigation

    const gotoHomepage = () => navigate("/"); // Navigate to the Homepage
    const goToHome = () => navigate("/home"); // Navigate to home page after login

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-900 bg-opacity-90">
            {/* Form Container */}
            <div className="relative z-10 w-full max-w-md p-6 bg-gray-900 rounded-lg">
                {/* Logo */}
                <div className="text-center mb-6">
                    <h1 onClick={gotoHomepage} className="text-3xl font-bold px-4 font-orbitron cursor-pointer bg-gradient-to-tr from-yellow-300 text-transparent bg-clip-text hover:bg-yellow-400">Movesic</h1>
                </div>

                {/* Title */}
                <h2 className="mb-4 text-xl font-semibold text-center text-gray-300">
                    Sign in
                </h2>

                {/* Social Login Buttons */}
                <AuthButton />

                {/* Divider */}
                <div className="flex items-center justify-center mb-4">
                    <hr className="w-full border-gray-700" />
                    <span className="px-4 text-gray-400 text-sm">OR</span>
                    <hr className="w-full border-gray-700" />
                </div>

                {/* Registration Form */}
                <form>
                    <div className="space-y-4">
                        <div>
                            <input
                                type="text"
                                placeholder="Username"
                                className="input1"
                            />
                        </div>
                        <div>
                            <input
                                type="password"
                                placeholder="Password"
                                className="input1"
                            />
                        </div>
                        <a href="/forgotpassword" className="text-yellow-400 hover:underline text-sm p-1">
                            Forgot password?
                        </a>
                    </div>

                    {/* Sign Up Button */}
                    <button
                        onClick={goToHome}
                        type="submit"
                        className="w-full mt-6 px-4 py-2 bg-yellow-400 text-gray-900 rounded-md hover:bg-yellow-600 font-bold"
                    >
                        Log in
                    </button>
                </form>

                {/* Already Have an Account */}
                <p className="mt-4 text-center text-sm text-gray-400">
                    Don't have an account?{" "}
                    <a href="/register" className="text-yellow-400 hover:underline">
                        Sign up
                    </a>
                </p>
            </div>
        </div>
    );
};

export default Login;