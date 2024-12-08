import { useState } from "react";
import Navbar from "../navbar/Navbar"

import { CiSquareQuestion } from "react-icons/ci";
import { AiOutlineUpload } from "react-icons/ai";


const IdentifySong = () => {
    const [url, setUrl] = useState(""); // For the URL input
    const [file, setFile] = useState(null); // For the uploaded file

    const handleFileChange = (event) => {
        setFile(event.target.files[0]);
    };

    const handleSubmit = () => {
        // Add logic for handling song identification
        console.log("URL:", url);
        console.log("File:", file);
    };

    return (
        <div className="bg-gray-900 text-white min-h-screen min-w-fit">
            {/* Navigation Bar */}
            <Navbar />

            {/* Identify Section */}
            <div className="flex flex-col items-center justify-center mt-16">
                <h1 className="text-5xl font-bold mb-4">
                    IDENTIFY SONGS
                </h1>
                <p className="text-lg text-gray-300 mb-4">
                    Recognize and match songs through URL or Uploaded file
                </p>

                {/* Input Fields */}
                <div className="flex flex-col space-y-4 items-center bg-gray-900 rounded-lg p-6 w-[32rem] shadow-lg">

                    {/* URL Input */}
                    <input
                        type="text"
                        placeholder="Enter URL"
                        value={url}
                        onChange={(e) => setUrl(e.target.value)}
                        className="w-full p-3 bg-gray-800 rounded-md text-gray-300 focus:outline-none focus:ring-2 focus:ring-yellow-400"
                    />

                    {/* File Upload */}
                    <div className="flex flex-col items-center justify-center w-full h-44 bg-gradient-to-tr from-gray-700 rounded-md cursor-pointer hover:bg-gray-700">
                        <label
                            htmlFor="file-upload"
                            className="flex flex-col items-center w-full h-full justify-center cursor-pointer"
                        >
                            <AiOutlineUpload className="text-4xl mb-2" />
                            <span className="text-gray-300 text-lg">
                                {file ? file.name : "Upload a file"}
                            </span>
                        </label>
                        <input
                            id="file-upload"
                            type="file"
                            onChange={handleFileChange}
                            className="hidden"
                        />
                    </div>

                    {/* Submit Button */}
                    <button
                        onClick={handleSubmit}
                        className="w-full bg-gradient-to-tr from-yellow-400 hover:bg-yellow-400 text-black text-xl font-bold py-3 rounded-md"
                    >
                        Identify
                    </button>
                </div>
            </div>

            <div className="fixed bottom-8 right-8 text-5xl text-yellow-400 cursor-pointer hover:text-yellow-600">
                <CiSquareQuestion />
            </div>
        </div>
    )
}

export default IdentifySong