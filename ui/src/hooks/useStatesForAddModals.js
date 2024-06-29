// this states are for the add database modal and add experiment modal
import { useState } from "react";

export const useStatesForAddModals = (initialState) => {
  const [columns, setColumns] = useState([{ columnName: "", columnType: "" }]);
  const [selectedDatasetOption, setSelectedDatasetOption] = useState({
    option: "Choose Dataset",
    value: "choose",
  });
  const [selectedDatasetId, setSelectedDatasetId] = useState(null);
  const [selectedDatasetData, setSelectedDatasetData] = useState(null);
  const [activeDataset, setActiveDataset] = useState(initialState);

  const handleAddColumn = () => {
    setColumns([...columns, { columnName: "", columnType: "" }]);
  };

  const resetState = () => {
    setColumns([{ columnName: "", columnType: "" }]);
    setSelectedDatasetOption({ option: "Choose Dataset", value: "choose" });
    setSelectedDatasetId(null);
    setSelectedDatasetData(null);
    setActiveDataset(initialState); // Reset activeDataset to the initial state
  };

  return {
    columns,
    setColumns,
    selectedDatasetOption,
    setSelectedDatasetOption,
    selectedDatasetId,
    setSelectedDatasetId,
    selectedDatasetData,
    setSelectedDatasetData,
    activeDataset,
    setActiveDataset,
    handleAddColumn,
    resetState,
  };
};
