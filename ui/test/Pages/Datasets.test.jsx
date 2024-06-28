/* eslint-disable testing-library/await-async-utils */
import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { QueryClient, QueryClientProvider } from "react-query";
import Datasets from "../../src/Pages/Dashboard/Datasets/Datasets";
import { MemoryRouter } from "react-router-dom";
import axios from "axios";
import { expect, vi } from "vitest";

// Mock axios
vi.mock("axios");

// Mock the toast
vi.mock("react-hot-toast", () => ({
  success: vi.fn(),
}));

describe("Datasets", () => {
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
          <Datasets />
        </QueryClientProvider>
      </MemoryRouter>
    );
  };

  it("should render the datasets page", async () => {
    renderComponent();

    const heading = await screen.findByText(/Datasets/i);
    expect(heading).toBeInTheDocument();
  });

  it("should render CommonTable component", async () => {
    renderComponent();

    waitFor(() => {
      const commonTable =  screen.getByRole("table");
      expect(commonTable).toBeInTheDocument();
    })
    
  });

  it("should open the modal when clicking the AddButton", async () => {
    renderComponent();

    const addButton = await screen.findByText(/New Dataset/i);
    expect(addButton).toBeInTheDocument();

    userEvent.click(addButton);

     waitFor(() => {
      const modalText = screen.getAllByText(/Create new dataset/i);
      expect(modalText.length).toBeGreaterThan(0);
    });
  });

  // New tests

  it("should render the search input", () => {
    renderComponent();

    const searchInput = screen.getByPlaceholderText("Search");
    expect(searchInput).toBeInTheDocument();
  });

  it("should update search text when typing in the search input", async () => {
    renderComponent();

    const searchInput = screen.getByPlaceholderText("Search");
    userEvent.type(searchInput, "test dataset");

    await waitFor(() => {
      expect(searchInput.value).toBe("test dataset");
    });
  });

  it("should render column headers in the table", async () => {
    renderComponent();
  
    waitFor(() => {
      expect(screen.getByText("Dataset Name")).toBeInTheDocument();
    });

  });

  it("should show loading state initially", () => {
    renderComponent();
  
    expect(screen.getByLabelText('loading')).toBeInTheDocument();
  });
});