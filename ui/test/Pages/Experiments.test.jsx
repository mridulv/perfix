/* eslint-disable no-unused-vars */
import React from "react";
import { it, expect, describe } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import Experiment from "../../src/Pages/Dashboard/Experiment/Experiment";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import userEvent from "@testing-library/user-event";

describe("Experiments", () => {
  const renderComponent = () => {
    const client = new QueryClient({
      defaultOptions: {
        queries: {
          retry: false,
        },
      },
    });

    render(
      <MemoryRouter>
        <QueryClientProvider client={client}>
          <Experiment />
        </QueryClientProvider>
      </MemoryRouter>
    );
  };
  it("should render the experiment page", async () => {
    renderComponent();

    const headings = await screen.findAllByText(/Experiments/i);
    headings.forEach((heading) => {
        expect(heading).toBeInTheDocument();
    })
  });

  it("should render CommonTable component", async () => {
    renderComponent();
  
    await waitFor(() => {
      const commonTable = screen.getByRole("table");
      expect(commonTable).toBeInTheDocument();
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
    userEvent.type(searchInput, "test experiment");

    await waitFor(() => {
      expect(searchInput.value).toBe("test experiment");
    });
  });

  it("should render column headers in the table", async () => {
    renderComponent();
  
    await waitFor(() => {
      expect(screen.getByText("Experiment Name")).toBeInTheDocument();
    });

  });

  it("should show loading state initially", () => {
    renderComponent();
  
    expect(screen.getByLabelText('loading')).toBeInTheDocument();
  });
});
