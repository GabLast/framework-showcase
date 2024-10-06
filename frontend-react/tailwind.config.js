/** @type {import('tailwindcss').Config} */

const withMT = require("@material-tailwind/react/utils/withMT");

// export default {
  module.exports = withMT({
  content: ["./index.html", "./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {},
  },
  plugins: [],
  // plugins: [require("daisyui")],
  // daisyui: {
  //   themes: [
  //     {
  //       mytheme: {
  //         primary: "#60a5fa",

  //         secondary: "#93c5fd",

  //         accent: "#ede9fe",

  //         neutral: "#e0e7ff",

  //         "base-100": "#f3f4f6",

  //         info: "#fef08a",

  //         success: "#00ff00",

  //         warning: "#eab308",

  //         error: "#ff0000",
  //       },
  //     },
  //   ],
  // },
});
// };
