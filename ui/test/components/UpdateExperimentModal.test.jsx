import React from 'react';
import { render, screen } from '@testing-library/react';
import UpdateExperimentModal from "../../src/components/UpdateExperimentModal"
import { vi } from 'vitest';

describe('UpdateExperimentModal', () => {
    const renderComponent = (openValue) => {
        const actionLabel = "Update Experiment";
        const mockOnClose = vi.fn();
        const refetch = vi.fn();
        const setSelectedExperiment = vi.fn();
        const buttonValue = "Update";
        const fields = [
            {
                name: "name",
                label: "Experiment Name",
                type: "text",
                placeholder: "Experiment Name",
              },
              {
                name: "writeBatchSize",
                label: "Write Batch Size",
                type: "number",
                placeholder: "Write Batch Size",
              },
        ]

        render(<UpdateExperimentModal
                open={openValue}
                onClose={mockOnClose}
                refetch={refetch}
                setSelectedExperiment={setSelectedExperiment}
                buttonValue={buttonValue}
                actionLabel={actionLabel}
                fields={fields}
             />)
    }
    it('should render the modal when the open is true', () => {
        
    })
})