import React from "react";
import { render, screen } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import Datasets from "../../src/Pages/Dashboard/Datasets/Datasets";
import { MemoryRouter } from "react-router-dom";
import axios from "axios";
import { vi } from "vitest";

// Mock axios
vi.mock("axios");

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

    const commonTable = await screen.findByRole("table");
    expect(commonTable).toBeInTheDocument();
  });
});