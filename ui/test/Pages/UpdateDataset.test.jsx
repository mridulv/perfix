import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from 'react-query';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import UpdateDataset from '../../src/Pages/Dashboard/Datasets/UpdateDataset';
import { datasets } from '../mocks/data';

describe('UpdateDataset', () => {
  const renderComponent = (datasetId) => {
    const client = new QueryClient();
    render(
      <QueryClientProvider client={client}>
        <MemoryRouter initialEntries={[`/update-dataset/${datasetId}`]}>
          <Routes>
            <Route path="/update-dataset/:id" element={<UpdateDataset />} />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>
    );
  };


  it('should render the update dataset page', async () => {
    const datasetId = 1;
    renderComponent(datasetId);

    
    waitFor(() => {
        const pageHeading =  screen.getByText(/Update Dataset/i);
        expect(pageHeading).toBeInTheDocument();
    })
  });

  it('should fetch and display the dataset details', async () => {
    const datasetId = 1;
    renderComponent(datasetId);

    const expectedDataset = datasets.find((dataset) => dataset.id.id === datasetId);

    waitFor(() => {
        const datasetName =  screen.getByText(expectedDataset.name);
        expect(datasetName).toBeInTheDocument();
    })
    
  });

  it('should fetch and display all available datasets', async () => {
    const datasetId = 1;
    renderComponent(datasetId);

    waitFor(() => {
        const allDatasets = screen.getAllByText(/Dataset \d+/i);
        expect(allDatasets).toHaveLength(datasets.length);
    })

    
  });
});