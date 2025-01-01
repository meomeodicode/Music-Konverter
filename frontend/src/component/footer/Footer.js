import React from 'react'
import { AiFillYoutube } from "react-icons/ai";
import { AiFillSpotify } from "react-icons/ai";

const Footer = () => {
    return (
        <footer className="bg-gray-800 text-white py-8">
            <div className="max-w-[84rem] mx-auto px-4">
                <div className="flex justify-between items-center mb-4">
                    <div className="text-4xl font-bold font-orbitron">
                        Movesic
                    </div>
                    <div className="flex space-x-12 text-lg">
                        <a href="/about" className="hover:text-yellow-400">About</a>
                        <a href="/privacy" className="hover:text-yellow-400">Privacy Policy</a>
                        <a href="/terms" className="hover:text-yellow-400">Terms of Service</a>
                        <a href="/contact" className="hover:text-yellow-400">Contact Us</a>
                    </div>
                </div>

                {/* Social Media Icons with Links */}
                <div className="flex justify-center space-x-12 mb-4">
                    {/* Spotify Icon with Link */}
                    <a href="https://www.spotify.com" target="_blank" rel="noopener noreferrer">
                        <AiFillSpotify className="text-5xl cursor-pointer hover:text-yellow-400" />
                    </a>

                    {/* YouTube Music Icon with Link */}
                    <a href="https://music.youtube.com" target="_blank" rel="noopener noreferrer">
                        <AiFillYoutube className="text-5xl cursor-pointer hover:text-yellow-400" />
                    </a>
                </div>

                {/* Copyright Section */}
                <div className="text-center text-sm text-gray-400">
                    &copy; 2024 Movesic. All rights reserved.
                </div>
            </div>
        </footer>
    )
}

export default Footer