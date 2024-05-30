/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,js,jsx}"],
  theme: {
    extend: {
      colors: {
        primary: "#3DA5FF",
        accent: "#F0F8FF"
      }
    },
  },
  plugins: [require('daisyui')],
}