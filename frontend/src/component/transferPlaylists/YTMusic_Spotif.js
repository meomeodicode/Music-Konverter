import React from 'react';
import React from 'react'
import { FaExchangeAlt } from 'react-icons/fa';

const YTMusic_Spotif = () => {
    return (
        <div className=" bg-gray-900 text-white min-h-screen min-w-fit">
            {/* Navigation Bar */}
            <Navbar />

            {/* Transfer Section */}
            <div className="flex items-center justify-center mt-4">
                <div className="flex-1 text-right">
                    <h1 className="text-3xl font-bold">YouTube Music</h1>
                </div>
                <div className="flex justify-center px-12 mt-1">
                    <FaExchangeAlt className="text-2xl text-yellow-400 hover:text-yellow-600" />
                </div>
                <div className="flex-1 text-left">
                    <h1 className="text-3xl font-bold">Spotify</h1>
                </div>
            </div>

            <div className="flex flex-row mt-8 px-32 h-[28rem]" ref={containerRef}>
                {/* Playlists Section */}
                <div
                    className={`overflow-y-scroll scrollbar transition-all duration-1000 bg-gray-800 rounded-lg p-4 ${selectedPlaylist ? "w-1/3" : "w-full"}`}
                >
                    <h2 className="text-2xl font-bold mb-4">My Playlists</h2>
                    {playlists.length > 0 ? (
                        <ul>
                            {playlists.map((playlist) => (
                                <li
                                    key={playlist.id}
                                    onClick={() => handlePlaylistClick(playlist)}
                                    className={`flex items-center justify-center cursor-pointer p-2 rounded-lg ${selectedPlaylist?.id === playlist.id ? "text-yellow-400" : "hover:bg-gray-700"
                                        }`}
                                >
                                    {playlist.thumbnailUrl ? (
                                        <img
                                            src={playlist.thumbnailUrl}
                                            alt="Playlist Thumbnail"
                                            className="w-12 h-12 rounded-lg mr-4"
                                        />
                                    ) : (
                                        <div className="text-xl w-12 h-12 rounded-lg mr-2 flex items-center justify-center">
                                            <PiPlaylistDuotone />
                                        </div>
                                    )}
                                    <span>{playlist.name}</span>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p>No playlists available</p>
                    )}
                </div>

                {/* Songs Section */}
                {selectedPlaylist && (
                    <div className="overflow-y-scroll scrollbar w-2/3 bg-gray-700 rounded-lg p-4 ml-4">
                        <h2 className="text-2xl font-bold mb-4">{selectedPlaylist.name}</h2>
                        <ul>
                            {songs.length > 0 ? (
                                songs.map((song) => (
                                    <li key={song.id} className="flex items-center p-2 border-b border-gray-600">
                                        <img
                                            src={song.thumbnailUrl}
                                            alt="Thumbnail"
                                            className="w-12 h-12 rounded-lg mr-4"
                                        />
                                        <div className="flex-1">
                                            <p className="font-bold text-lg truncate">{song.trackName}</p>
                                            <p className="text-sm text-gray-400 truncate">{song.artistNames.join(", ")}</p>
                                        </div>
                                        <div className="text-gray-400 ml-4">
                                            {calculateDuration(song.duration)}
                                        </div>
                                    </li>
                                ))
                            ) : (
                                <p>No songs available</p>
                            )}
                        </ul>
                        <div className="flex justify-center items-center">
                            <button
                                onClick={handleTransferClick}
                                className="mt-4 px-4 py-2 bg-yellow-400 text-gray-900 font-bold rounded-lg hover:bg-yellow-500"
                            >
                                Transfer
                            </button>
                        </div>
                    </div>
                )}
            </div>

            {/* Help Section */}
            <div className="fixed bottom-8 right-8 text-5xl text-yellow-400 cursor-pointer hover:text-yellow-600">
                <CiSquareQuestion />
            </div>
        </div>
    );
};

export default YTMusic_Spotif