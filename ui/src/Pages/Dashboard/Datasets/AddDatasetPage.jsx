import React, { useState } from 'react';
import AddDataset from '../../../components/AddDataset';

const AddDatasetPage = () => {
    const [columns, setColumns] = useState([{ columnName: "", columnType: "" }]);

    const handleAddColumn = () => {
        setColumns([...columns, { columnName: "", columnType: "" }]);
      };
    return (
        <div>
            <AddDataset columns={columns} handleAddColumn={handleAddColumn}/>
        </div>
    );
};

export default AddDatasetPage;