import { defineConfig, devices } from "@playwright/test";

export default defineConfig({
  testDir: "./tests",

  timeout: 30000,

  expect: {
    timeout: 5000
  },

  fullyParallel: false,

  workers: 1,

  reporter: "html",

  use: {
    baseURL: "http://localhost:5173",
    trace: "on-first-retry"
  },

  webServer: [
    {
      command: "mvn spring-boot:run -Dspring-boot.run.profiles=ui-test",
      cwd: "../backend",
      url: "http://localhost:8080/api/products",
      reuseExistingServer: true,
      timeout: 120000
    },
    {
      command: "npm run dev -- --host 127.0.0.1",
      cwd: ".",
      url: "http://localhost:5173",
      reuseExistingServer: true,
      timeout: 120000
    }
  ],

  projects: [
    {
      name: "chromium",
      use: {
        ...devices["Desktop Chrome"]
      }
    }
  ]
});