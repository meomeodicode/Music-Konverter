/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        exo2: ['"Exo 2"'],
        orbitron: ['Orbitron']
      }
    },
  },
  plugins: [],
}