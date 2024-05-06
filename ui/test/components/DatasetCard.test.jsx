import React from 'react';
import { it, expect, describe } from 'vitest';
import DatasetCard from '../../src/components/DatasetCard';
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom';

describe('DatasetCard', () => {
    it('should render name of the dataset when provided the dataset', () => {
        const dataset = {
            id: { id: 2145983659 },
            name: "First Dataset",
            rows: 10000,
            columns: [
              {
                columnName: "studentName",
                columnType: { type: "NameType", isUnique: true },
              },
              {
                columnName: "studentAddress",
                columnType: { type: "AddressType", isUnique: true },
              },
            ],
          };
        render(<MemoryRouter><DatasetCard dataset={dataset}/></MemoryRouter>);

        const heading = screen.getByRole("heading", {name: `name: ${dataset.name}`});
        expect(heading).toBeInTheDocument();
    });
    it('should render the link with the correct url', () => {
        const dataset = {
            id: { id: 2145983659 },
            name: "First Dataset",
            rows: 10000,
            columns: [
              {
                columnName: "studentName",
                columnType: { type: "NameType", isUnique: true },
              },
              {
                columnName: "studentAddress",
                columnType: { type: "AddressType", isUnique: true },
              },
            ],
          };
        render(<MemoryRouter><DatasetCard dataset={dataset}/></MemoryRouter>);

        const link = screen.getByRole("link", {name: "See Details"});
        expect(link).toHaveAttribute("href", `/datasets/${dataset.id.id}`);
    });
})