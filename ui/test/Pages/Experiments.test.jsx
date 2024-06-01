import React from "react";
import { it, expect, describe } from "vitest";
import { render, screen } from "@testing-library/react";
import Experiment from "../../src/Pages/Dashboard/Experiment/Experiment";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";

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
});
