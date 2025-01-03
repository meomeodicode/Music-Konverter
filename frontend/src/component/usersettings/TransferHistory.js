import React, { useState } from "react";

const TransferHistory = () => {
    // Dummy transfer data
    const [transfers, setTransfers] = useState([
        {
            title: "1 song playlist",
            type: "Playlist",
            source: "Spotify",
            destination: "YouTube Music",
            finished: "01/03/2025",
        },
    ]);
    const [selectedPlaylist, setSelectedPlaylist] = useState(null);

    // Dummy data for playlist songs
    const dummyPlaylistSongs = [
        "Faded - Alan Walker"
    ];

    const closeModal = () => {
        setSelectedPlaylist(null);
    };

    return (
        <div className="p-8">
            {/* Table Header */}
            <h2 className="text-2xl font-bold mb-4 mt-8">Transfer History</h2>
            <table className="w-full table-auto border-collapse border border-gray-700 max-h-[32rem] overflow-y-scroll scrollbar">
                <thead>
                    <tr className="rounded-sm text-white">
                        <th className="border border-gray-700 border-b-gray-400 px-4 py-2 text-left">Title</th>
                        <th className="border border-gray-700 border-b-gray-400 px-4 py-2 text-left">Type</th>
                        <th className="border border-gray-700 border-b-gray-400 px-4 py-2 text-left">Source</th>
                        <th className="border border-gray-700 border-b-gray-400 px-4 py-2 text-left">Destination</th>
                        <th className="border border-gray-700 border-b-gray-400 px-4 py-2 text-left">Finished</th>
                    </tr>
                </thead>
                <tbody>
                    {transfers.length === 0 ? (
                        // Display "No transfer history" message
                        <tr>
                            <td colSpan="5" className="text-center py-4 text-gray-400">
                                No transfer history
                            </td>
                        </tr>
                    ) : (
                        // Display transfer rows
                        transfers.map((transfer, index) => (
                            <tr key={index} className="text-gray-200">
                                <td
                                    className="border border-gray-700 px-4 py-2 cursor-pointer text-yellow-400 hover:underline"
                                    onClick={() => setSelectedPlaylist(transfer)}
                                >
                                    {transfer.title}
                                </td>
                                <td className="border border-gray-700 px-4 py-2">{transfer.type}</td>
                                <td className="border border-gray-700 px-4 py-2">{transfer.source}</td>
                                <td className="border border-gray-700 px-4 py-2">{transfer.destination}</td>
                                <td className="border border-gray-700 px-4 py-2">{transfer.finished}</td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {/* Modal for displaying playlist songs */}
            {selectedPlaylist && (
                <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
                    <div className="bg-gray-800 p-6 rounded-lg shadow-lg max-w-md w-full max-h-80 overflow-y-scroll scrollbar">
                        <h2 className="text-xl font-bold text-white mb-4">
                            Playlist: {selectedPlaylist.title}
                        </h2>
                        <ul className="text-gray-200 mb-4">
                            {dummyPlaylistSongs.map((song, index) => (
                                <li key={index} className="py-1">
                                    {song}
                                </li>
                            ))}
                        </ul>
                        <button
                            onClick={closeModal}
                            className="px-4 py-2 bg-red-600 hover:bg-red-500 text-white rounded"
                        >
                            Close
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default TransferHistory;
