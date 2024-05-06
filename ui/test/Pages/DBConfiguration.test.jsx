import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import DBConfiguration from "../../src/Pages/Dashboard/DBConfiguration/DBConfiguration";
import { expect } from "vitest";
import userEvent from "@testing-library/user-event";

describe("DBConfiguration", () => {
  const renderComponent = () => {
    const client = new QueryClient();

    render(
      <QueryClientProvider client={client}>
        <DBConfiguration />
      </QueryClientProvider>
    );
  };
  it("should render the  database configurations page", () => {
    renderComponent();

    waitFor(() => {
      const heading = screen.getByRole("heading");
      expect(heading).toBeInTheDocument();
    });
  });
  it("should display configurations if present", async () => {
    renderComponent();
    waitFor(() => {
      const configurationElements = screen.getAllByText(/Configuration Name:/);
      expect(configurationElements).toBeTruthy();
    });
  });

  it("should navigate to the add configuration page when add configuration button is clicked", async () => {
    renderComponent();
    const user = userEvent.setup();
     waitFor(async() => {
      const addConfigurationButton = screen.getByRole("link", {
        name: /Add Configuration/i,
      });
      await user.click(addConfigurationButton)
      expect(window.location.pathname).toBe("/add-db-configuration");
    });
  });
});
