import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from 'react-query';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import DatasetDetails from '../../src/Pages/Dashboard/Datasets/DatasetDetails';

describe('DatasetDetails', () => {
  const renderComponent = (datasetId) => {
    const client = new QueryClient();
    render(
      <QueryClientProvider client={client}>
        <MemoryRouter initialEntries={[`/dataset/${datasetId}`]}>
          <Routes>
            <Route path="/dataset/:id" element={<DatasetDetails />} />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>
    );
  };

  it('should render the correct dataset details', async () => {
    const datasetId = 1;
    renderComponent(datasetId);

    

    await waitFor(() => {
      const datasetName = screen.getByText(/dataset/i);
      expect(datasetName).toBeInTheDocument();
    });
  });
});