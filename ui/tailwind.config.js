/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: "#3DA5FF",
        secondary: "#BDE0FF",
        accent: "#F0F8FF"
      }
    },
  },
  plugins: [
    require('daisyui'),
  ],
}
