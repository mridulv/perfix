import React from "react";
import { render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from "react-query";
import Datasets from "../../src/Pages/Dashboard/Datasets/Datasets"
import { expect } from "vitest";

describe('Datasets', () => {
    const renderComponent = () => {
        const client = new QueryClient();

        render(
            <QueryClientProvider client={client}>
                <Datasets/>
            </QueryClientProvider>
        )
    }
    it('should render the datasets page', async() => {
        renderComponent();
        
        waitFor(() => {
            const heading =  screen.getByRole("heading");
            expect(heading).toBeInTheDocument();
        })
    });

    it('should render datasets card', async() => {
        renderComponent();

        waitFor(() => {
            const text = screen.getAllByText(/name/i);
            expect(text.length).toBeGreaterThan(0)
        })
    })
})