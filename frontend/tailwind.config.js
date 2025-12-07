/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        accent: "#3bf59b",
        ink: "#0b1117",
        slate: {
          950: "#05080c"
        }
      },
      fontFamily: {
        mono: ["'IBM Plex Mono'", "ui-monospace", "SFMono-Regular", "Menlo", "monospace"],
        display: ["'Space Grotesk'", "Inter", "system-ui", "sans-serif"]
      },
      backgroundImage: {
        "grid-scan": "radial-gradient(circle at 1px 1px, rgba(59, 245, 155, 0.15) 1px, transparent 0)"
      }
    },
  },
  plugins: [],
};
