import React, { useState } from "react";

const Account = ({ currentAvatar, onAvatarChange }) => {
  const [username, setUsername] = useState("Demo Nguyễn");
  const [email] = useState("nguyendemo76@gmail.com");
  const [avatar, setAvatar] = useState(currentAvatar); // Avatar state

  const handleAvatarUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        setAvatar(reader.result); // Update local avatar state
      };
      reader.readAsDataURL(file);
    }
  };

  return (
    <div className="space-y-6">
      <h2 className="text-3xl font-bold text-gray-200 mb-4">Account Settings</h2>
      <div className="space-y-4">
        {/* Avatar */}
        <div>
          <div className="flex flex-col items-center">
            <img
              src={avatar || "http://www.gravatar.com/avatar/?d=mp"}
              alt="Avatar"
              className="w-20 h-20 rounded-full mb-2 object-cover"
            />
            <label
              htmlFor="avatar-upload"
              className="cursor-pointer px-2 py-2 bg-yellow-400 hover:bg-yellow-500 text-gray-900 font-bold rounded-lg text-sm"
            >
              Upload Avatar
            </label>
            <input
              id="avatar-upload"
              type="file"
              accept="image/*"
              className="hidden"
              onChange={handleAvatarUpload}
            />
          </div>
        </div>

        {/* Username */}
        <div>
          <label className="block text-lg font-medium text-gray-400">Username</label>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className="input1"
          />
        </div>

        {/* Email */}
        <div>
          <label className="block text-lg font-medium text-gray-400">Email address</label>
          <div className="w-full px-2 py-2 rounded-md bg-gray-800 text-gray-200"> {email} </div>
        </div>

        {/* Password */}
        <div>
          <label className="block text-lg font-medium text-gray-400">Current password</label>
          <input
            type="password"
            placeholder="Current password"
            className="input1"
          />
        </div>
        <div className="flex items-center justify-between text-lg text-yellow-400">
          <a href="#" className="hover:underline">
            Change my password
          </a>
          <a href="#" className="hover:underline">
            Forgot password?
          </a>
        </div>
      </div>
      <button className="mt-6 px-4 py-2 bg-yellow-400 text-gray-900 font-bold rounded-lg hover:bg-yellow-500">
        Save
      </button>
    </div >
  );
};

export default Account; 
