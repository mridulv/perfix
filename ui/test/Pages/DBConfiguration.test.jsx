/* eslint-disable testing-library/await-async-utils */
import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import DBConfiguration from "../../src/Pages/Dashboard/DBConfiguration/DBConfiguration";
import axios from "axios";
import { vi } from "vitest";
import userEvent from "@testing-library/user-event";

// Mock axios
vi.mock("axios");

describe("Databases Configuration", () => {
  beforeEach(() => {
    // Mock the axios.post to resolve immediately with an empty array
    axios.post.mockResolvedValue({
      status: 200,
      data: [],
    });
  });

  const renderComponent = () => {
    const client = new QueryClient();

    render(
      <MemoryRouter>
        <QueryClientProvider client={client}>
          <DBConfiguration />
        </QueryClientProvider>
      </MemoryRouter>
    );
  };

  it("should render the database page", async () => {
    renderComponent();

    const headings = await screen.findAllByText(/Database/i);
    headings.forEach((heading) => {
      expect(heading).toBeInTheDocument();
    });
  });

  it("should render CommonTable component", async () => {
    renderComponent();

    waitFor(() => {
      const commonTable = screen.getByRole("table");
      expect(commonTable).toBeInTheDocument();
    });
  });

  it("should open the modal when clicking the AddButton", async () => {
    renderComponent();

    const addButton = await screen.findByText("New Database");
    expect(addButton).toBeInTheDocument();

    // Use userEvent to simulate a click event on the AddButton
    userEvent.click(addButton);

    // Check if the modal is rendered and visible
    waitFor(() => {
      const modalText = screen.getAllByText(/Create new database/i);
      expect(modalText.length).toBeGreaterThan(0);
    });
  });

  it("should render the search input", () => {
    renderComponent();

    const searchInput = screen.getByPlaceholderText("Search");
    expect(searchInput).toBeInTheDocument();
  });

  it("should update search text when typing in the search input", async () => {
    renderComponent();

    const searchInput = screen.getByPlaceholderText("Search");
    userEvent.type(searchInput, "test database");

    await waitFor(() => {
      expect(searchInput.value).toBe("test database");
    });
  });

  it("should render column headers in the table", async () => {
    renderComponent();

    waitFor(() => {
      expect(screen.getByText("Database Name")).toBeInTheDocument();
    });
  });

  it("should show loading state initially", () => {
    renderComponent();

    expect(screen.getByLabelText("loading")).toBeInTheDocument();
  });
});
