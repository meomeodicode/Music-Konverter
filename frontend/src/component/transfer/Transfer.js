import Navbar from "../navbar/Navbar";

import { useState } from "react";

import { CiSquareQuestion } from "react-icons/ci";
import { FaSpotify, FaApple } from "react-icons/fa";
import { SiTidal } from "react-icons/si";
import { SiYoutubemusic } from "react-icons/si";




const Transfer = () => {
    const [source, setSource] = useState(null); // Track selected source
    const [destination, setDestination] = useState(null); // Track selected destination
    const [showSourceOptions, setShowSourceOptions] = useState(false); // Toggle source options visibility
    const [showDestinationOptions, setShowDestinationOptions] = useState(false); // Toggle destination options visibility

    const services = [
        { name: "Spotify", icon: <FaSpotify className="text-green-400 text-5xl mr-2" /> },
        { name: "YouTube Music", icon: <SiYoutubemusic className="text-red-500 text-5xl mr-2" /> },
        { name: "Apple Music", icon: <FaApple className="text-gray-400 text-5xl mr-2" /> },
        { name: "Tidal", icon: <SiTidal className="text-gray-400 text-5xl mr-2" /> },
    ];

    // Filter options to prevent duplicate selections
    const availableSourceServices = services.filter(service => service.name !== destination);
    const availableDestinationServices = services.filter(service => service.name !== source);

    return (
        <div className="bg-gray-900 text-white min-h-screen min-w-fit">
            {/* Navigation Bar */}
            <Navbar />

            {/* Transfer Section */}
            <div className="flex flex-col items-center justify-center mt-16">
                <h1 className="text-5xl font-bold mb-4">
                    TRANSFER PLAYLISTS
                </h1>
                <p className="text-lg text-gray-300 mb-4">
                    Transfer your playlists to another service
                </p>

                <div className="flex items-center justify-center space-x-12">
                    {/* Source Card */}
                    <div
                        className="flex flex-col items-center justify-center bg-gradient-to-tr from-gray-800 rounded-lg p-6 w-80 h-80 shadow-lg cursor-pointer hover:bg-gray-700 relative"
                        onClick={() => setShowSourceOptions(!showSourceOptions)}
                    >
                        {showSourceOptions ? (
                            <div className="absolute top-0 left-0 w-full h-full bg-gradient-to-tr from-gray-800 rounded-lg p-8 flex flex-col items-center space-y-4 overflow-y-scroll scrollbar">
                                <p className="text-2xl text-gray-300 font-bold mb-4">Choose a service</p>
                                {availableSourceServices.map(service => (
                                    <div
                                        key={service.name}
                                        className="flex flex-col items-center text-center px-4 py-2 text-gray-300 hover:bg-gray-600 cursor-pointer rounded-xl"
                                        onClick={() => {
                                            setSource(service.name);
                                            setShowSourceOptions(false);
                                        }}
                                    >
                                        {service.icon}
                                        <span className="mt-2 text-lg font-semibold">{service.name}</span>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <>
                                {source ? (
                                    <div className="flex items-center">
                                        {services.find(service => service.name === source)?.icon}
                                        <p className="text-3xl font-semibold ml-2">{source}</p>
                                    </div>
                                ) : (
                                    <>
                                        <div className="text-7xl text-gray-400 mb-4">+</div>
                                        <p className="text-2xl font-semibold">Select source</p>
                                    </>
                                )}
                            </>
                        )}
                    </div>

                    {/* Arrow */}
                    <div className="bg-gradient-to-tr from-yellow-400 text-transparent bg-clip-text text-[150px]">
                        &#10145;
                    </div>

                    {/* Destination Card */}
                    <div
                        className="flex flex-col items-center justify-center bg-gradient-to-tr from-gray-800 rounded-lg p-6 w-80 h-80 shadow-lg cursor-pointer hover:bg-gray-700 relative"
                        onClick={() => setShowDestinationOptions(!showDestinationOptions)}
                    >
                        {showDestinationOptions ? (
                            <div className="absolute top-0 left-0 w-full h-full bg-gradient-to-tr from-gray-800 rounded-lg p-8 flex flex-col items-center space-y-4 overflow-y-scroll scrollbar">
                                <p className="text-2xl text-gray-300 font-bold mb-4">Choose a service</p>
                                {availableDestinationServices.map(service => (
                                    <div
                                        key={service.name}
                                        className="flex flex-col items-center text-center px-4 py-2 text-gray-300 hover:bg-gray-600 cursor-pointer rounded-xl"
                                        onClick={() => {
                                            setDestination(service.name);
                                            setShowDestinationOptions(false);
                                        }}

                                    >
                                        {service.icon}
                                        <span className="mt-2 text-lg font-semibold">{service.name}</span>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <>
                                {destination ? (
                                    <div className="flex items-center">
                                        {services.find(service => service.name === destination)?.icon}
                                        <p className="text-3xl font-semibold ml-2">{destination}</p>
                                    </div>
                                ) : (
                                    <>
                                        <div className="text-7xl text-gray-400 mb-4">+</div>
                                        <p className="text-2xl font-semibold">Select destination</p>
                                    </>
                                )}
                            </>
                        )}
                    </div>
                </div>
            </div>

            {/* Help Section */}
            <div className="fixed bottom-8 right-8 text-5xl text-yellow-400 cursor-pointer hover:text-yellow-600">
                <CiSquareQuestion />
            </div>
        </div>
    );
}

export default Transfer