import React from "react";
import { render, screen } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import DBConfiguration from "../../src/Pages/Dashboard/DBConfiguration/DBConfiguration"
import axios from "axios";
import { vi } from "vitest";

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

    const commonTable = await screen.findByRole("table");
    expect(commonTable).toBeInTheDocument();
  });
});