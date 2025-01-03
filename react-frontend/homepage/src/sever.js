const express = require("express");
const bodyParser = require("body-parser");
const cors = require("cors");
const { Pool } = require("pg");

const app = express();
const PORT = 8080;

// PostgreSQL connection setup
const pool = new Pool({
  user: "your_username",
  host: "localhost",
  database: "your_database",
  password: "your_password",
  port: 5432,
});

// Middleware
app.use(cors());
app.use(bodyParser.json());

// Fetch converted playlist history for a specific user
app.get("/api/playlists/history/:userId", async (req, res) => {
  const { userId } = req.params;

  try {
    const query = `
      SELECT c.ConversionID, p.Title AS PlaylistTitle, p.Platform AS PlaylistPlatform, 
             c.SourcePF, c.TargetPF, c.ConversionDate,
             json_agg(json_build_object('Title', s.Title, 'Artist', s.Artist, 'Platform', s.Platform)) AS Songs
      FROM Conversions c
      JOIN Playlists p ON c.PlaylistID = p.PlaylistID
      JOIN Songs s ON s.PlaylistID = p.PlaylistID
      WHERE c.UserID = $1
      GROUP BY c.ConversionID, p.Title, p.Platform, c.SourcePF, c.TargetPF, c.ConversionDate
      ORDER BY c.ConversionDate DESC;
    `;
    const result = await pool.query(query, [userId]);
    res.json(result.rows);
  } catch (error) {
    console.error("Error fetching playlist history:", error.message);
    res.status(500).send("Error fetching playlist history");
  }
});

// Start the server
app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});
