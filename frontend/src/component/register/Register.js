import { useNavigate } from "react-router-dom";
import AuthButton from "../authbutton/AuthButton";

const Register = () => {
    const navigate = useNavigate(); // React Router's hook for programmatic navigation

    const gotoHomepage = () => navigate("/"); // Navigate to the Homepage

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
                    Create an account
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
                                type="email"
                                placeholder="Email"
                                className="input1"
                            />
                        </div>
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
                        <div>
                            <input
                                type="password"
                                placeholder="Repeat Password"
                                className="input1"
                            />
                        </div>
                    </div>

                    {/* Sign Up Button */}
                    <button
                        type="submit"
                        className="w-full mt-6 px-4 py-2 bg-yellow-400 text-gray-900 rounded-md hover:bg-yellow-600 font-bold"
                    >
                        Sign up
                    </button>
                </form>

                {/* Already Have an Account */}
                <p className="mt-4 text-center text-sm text-gray-400">
                    Already have an account?{" "}
                    <a href="/login" className="text-yellow-400 hover:underline">
                        Sign in
                    </a>
                </p>

                {/* Disclaimer */}
                <p className="mt-4 text-center text-xs text-gray-500">
                    By clicking "Sign up," you agree to our{" "}
                    <a href="/terms" className="text-yellow-400 hover:underline">
                        Terms & Conditions
                    </a>{" "}
                    and{" "}
                    <a href="/privacy" className="text-yellow-400 hover:underline">
                        Privacy Policy
                    </a>
                    .
                </p>
            </div>
        </div>
    );
};

export default Register;