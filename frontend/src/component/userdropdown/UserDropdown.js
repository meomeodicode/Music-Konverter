import React, { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";

import UserSettings from "../usersettings/UserSettings";

import { RxAvatar } from "react-icons/rx";
import { IoSettingsSharp } from "react-icons/io5";
import { IoLogOutOutline } from "react-icons/io5";
import { MdHistory } from "react-icons/md";
import { LuDatabaseBackup } from "react-icons/lu";

function UserDropdown() {
    const [isOpen, setIsOpen] = useState(false);
    const [settingsOpen, setSettingsOpen] = useState(false);
    const [selectedOption, setSelectedOption] = useState(null);
    const dropdownRef = useRef(null);

    // Toggle the dropdown
    const toggleDropdown = () => {
        setIsOpen(!isOpen);
    };

    // Close the dropdown if clicked outside
    useEffect(() => {
        const handleClickOutside = (e) => {
            if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
                setIsOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);

        // Cleanup listener on component unmount
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const navigate = useNavigate(); // React Router's hook for programmatic navigation

    const Logout = () => navigate("/"); // Navigate to the Login page

    const openUserSettings = (option) => {
        setSelectedOption(option); // Set the selected option
        setIsOpen(false); // Close the dropdown
        setSettingsOpen(true); // Open the user settings
    };

    const closeUserSettings = () => {
        setSettingsOpen(false);
        setSelectedOption(null);
    };

    return (
        <div className="relative inline-block text-left" ref={dropdownRef}>
            {/* User Button */}
            <button
                onClick={toggleDropdown}
                className="flex items-center justify-center px-4 py-2 bg-gradient-to-tr from-yellow-400 hover:bg-yellow-400 text-gray-900 text-xl rounded-lg font-bold"
            >
                <RxAvatar className="text-3xl mr-2" />
                <span className="truncate max-w-[80px]">Demo Nguyễn</span>
            </button>

            {/* Dropdown Menu */}
            {isOpen && (
                <div
                    className={`absolute left-1/2 -translate-x-1/2 mt-4 w-48 bg-white border border-gray-200 rounded-lg shadow-lg transform transition-all duration-200 ${isOpen ? "scale-100 opacity-100" : "scale-95 opacity-0"}`}
                >
                    <ul className="py-1">
                        <li
                            className="flex items-center px-4 py-2 hover:bg-gray-100 cursor-pointer text-gray-700"
                            onClick={() => openUserSettings("account")}
                        >
                            <RxAvatar className="text-xl mr-2" />
                            Account
                        </li>
                        <li
                            className="flex items-center px-4 py-2 hover:bg-gray-100 cursor-pointer text-gray-700"
                            onClick={() => openUserSettings("transferHistory")}
                        >
                            <MdHistory className="text-xl mr-2" />
                            Transfer history
                        </li>
                        <li
                            className="flex items-center px-4 py-2 hover:bg-gray-100 cursor-pointer text-gray-700"
                            onClick={() => openUserSettings("backups")}
                        >
                            <LuDatabaseBackup className="text-xl mr-2" />
                            Backups
                        </li>
                        <li
                            className="flex items-center px-4 py-2 hover:bg-gray-100 cursor-pointer text-gray-700"
                            onClick={() => openUserSettings("settings")}
                        >
                            <IoSettingsSharp className="text-xl mr-2" />
                            Settings
                        </li>
                        <li
                            onClick={Logout}
                            className="flex items-center px-4 py-2 hover:bg-gray-100 cursor-pointer text-red-500"
                        >
                            <IoLogOutOutline className="text-2xl mr-2" />
                            Logout
                        </li>
                    </ul>
                </div>
            )}

            {/* User Settings Window */}
            {settingsOpen && (
                <UserSettings
                    onClose={closeUserSettings}
                    defaultSelected={selectedOption} // Pass selected option to UserSettings
                />
            )}
        </div>
    );
}

export default UserDropdown;