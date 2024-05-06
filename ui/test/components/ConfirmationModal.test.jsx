import React from 'react';
import { render, screen } from '@testing-library/react';
import ConfirmationModal from '../../src/components/ConfirmationModal';
import userEvent from '@testing-library/user-event';

describe('ConfirmationModal', () => {
  
  const renderComponent = (openValue, actionText) => {
    const mockData = { name: 'Test Name', databaseConfigId: { id: '1234' } };
    const mockAction = vi.fn();
    const mockOnClose = vi.fn();
    const user = userEvent.setup();
    render(<ConfirmationModal
            open={openValue}
            data={mockData}
            action={mockAction}
            actionText={actionText} 
            onClose={mockOnClose}
        />);
    return { mockAction, mockOnClose, mockData, user };
  }

  it('renders the modal when open is true', () => {
    renderComponent(true, "Are you sure you want to delete this");

    expect(screen.getByText(/Want to delete this/i)).toBeInTheDocument();
  });

  it('does not render the modal when open is false', () => {
    renderComponent(false, "Delete");

    expect(screen.queryByText(/Delete Test Name?/i)).not.toBeInTheDocument();
  });


  it('calls action with correct argument when clicking "Yes" button', async() => {
    const {mockAction, mockData, user} = renderComponent(true, "Delete");


    await user.click(screen.getByRole('button', { name: 'Yes' }));
    expect(mockAction).toHaveBeenCalledWith(mockData.databaseConfigId.id);
  });

  it('calls onClose when clicking "No" button', async() => {
    const {mockOnClose, user} = renderComponent(true, "Delete")


    await user.click(screen.getByRole('button', { name: 'No' }));
    expect(mockOnClose).toHaveBeenCalledTimes(1);
  });
});