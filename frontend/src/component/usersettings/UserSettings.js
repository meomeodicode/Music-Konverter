import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Account from "./Account";
import TransferHistory from "./TransferHistory";

const UserSettings = ({ onClose, defaultSelected }) => {
    const [selectedOption, setSelectedOption] = useState(defaultSelected || "account");

    useEffect(() => {
        // Set selected option based on the defaultSelected prop
        setSelectedOption(defaultSelected);
    }, [defaultSelected]);

    const renderContent = () => {
        switch (selectedOption) {
            case "account":
                return <Account />;
            case "transferHistory":
                return <TransferHistory />;
            case "backups":
                return <div>Backups Content</div>;
            case "settings":
                return <div>Settings Content</div>;
            case "logout":
                return null;
            default:
                return null;
        }
    };

    const navigate = useNavigate();
    const Logout = () => navigate("/"); // Navigate to the Login page

    return (
        <div className="fixed inset-0 bg-gray-900 bg-opacity-90 flex">
            {/* Left Column - Options */}
            <div className="w-1/4 bg-gray-800 text-white p-6">
                <h2 className="text-2xl font-bold mb-4 mt-32">User Settings</h2>
                <ul className="space-y-2">
                    <li
                        className={`p-2 cursor-pointer rounded-lg text-lg ${selectedOption === "account" ? "bg-gray-700 text-yellow-500" : "hover:bg-gray-700"
                            }`}
                        onClick={() => setSelectedOption("account")}
                    >
                        Account
                    </li>
                    <li
                        className={`p-2 cursor-pointer rounded-lg text-lg ${selectedOption === "transferHistory" ? "bg-gray-700 text-yellow-500" : "hover:bg-gray-700"
                            }`}
                        onClick={() => setSelectedOption("transferHistory")}
                    >
                        Transfer History
                    </li>
                    <li
                        className={`p-2 cursor-pointer rounded-lg text-lg ${selectedOption === "backups" ? "bg-gray-700 text-yellow-500" : "hover:bg-gray-700"
                            }`}
                        onClick={() => setSelectedOption("backups")}
                    >
                        Backups
                    </li>
                    <li
                        className={`p-2 cursor-pointer rounded-lg text-lg ${selectedOption === "settings" ? "bg-gray-700 text-yellow-500" : "hover:bg-gray-700"
                            }`}
                        onClick={() => setSelectedOption("settings")}
                    >
                        Settings
                    </li>
                    <li
                        className={`p-2 cursor-pointer rounded-lg text-lg text-red-500 ${selectedOption === "logout" ? "bg-gray-700" : "hover:bg-gray-700"
                            }`}
                        onClick={Logout}
                    >
                        Log out
                    </li>
                </ul>
            </div>

            {/* Right Column - Content */}
            <div className="flex-1 bg-gray-700 text-white p-16 relative">
                <button
                    className="absolute top-2 right-6 text-5xl text-gray-300 hover:text-yellow-500"
                    onClick={onClose}
                >
                    &times;
                </button>
                {renderContent()}
            </div>
        </div>
    );
};

export default UserSettings;
