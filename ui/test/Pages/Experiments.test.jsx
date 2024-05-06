import React from 'react';
import { it, expect, describe } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import Experiment from "../../src/Pages/Dashboard/Experiment/Experiment";
import { QueryClient, QueryClientProvider } from 'react-query';

describe('Experiments', () => {
    const renderComponent = () => {
        const client = new QueryClient({
            defaultOptions: {
                queries: {
                    retry: false,
                },
            },
        });

        render(
            <QueryClientProvider client={client}>
                <Experiment/>
            </QueryClientProvider>
        )
    }
    it('should render the experiment page', () => {
        renderComponent();
        
        waitFor(() => {
            const heading = screen.getByRole("heading");
            expect(heading).toBeInTheDocument();
        })
    })
})