import {defineConfig} from "vitest/config";
import react from "@vitejs/plugin-react";

export default defineConfig({
    plugins: [react({ include: /\.(|js|jsx|ts|tsx)$/ })],
    test: {
        environment: "jsdom",
        globals: true,
        setupFiles: "./test/setup.js"
    }
})
