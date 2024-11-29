import React from "react";
import "./PlaylistCard.scss";

const PlaylistCard = ({
  uid,
  name,
  image,
  no_of_songs,
  duration,
  isSelected,
  handleCheckboxChange,
}) => {
  return (
    <div id={uid} className="music">
      <div
        id={uid}
        className={`music-content ${isSelected ? "active" : ""}`}
        onClick={handleCheckboxChange}
      >
        <div id={uid} className="image-wrapper">
          <img id={uid} src={image} alt="Playlist cover" />
        </div>
        <div id={uid} className="right">
          <span id={uid} className="name">
            {name.length > 19 ? name.substring(0, 19) + "..." : name}
          </span>
          <span id={uid} className="songs">
            {no_of_songs} tracks
          </span>
          <span id={uid} className="duration">
            {duration}
          </span>
        </div>
      </div>
    </div>
  );
};

export default PlaylistCard;
