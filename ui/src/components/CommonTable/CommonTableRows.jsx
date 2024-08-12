/* eslint-disable no-unused-vars */
import React from "react";
import DatasetTableRows from "./DatasetTableRows";
import DatabaseTableRows from "./DatabaseTableRows";
import ExperimentTableRows from "./ExperimentTableRows";
import UseCasesTableRows from "./UseCasesTableRows";

const CommonTableRows = ({
  data,
  columnHeads,
  showButtons,
  handleShowOptions,
  handleSelectedData,
  handleUpdateDatabase,
  refetch,
}) => {
  switch (columnHeads[0]) {
    case "Dataset Name":
      return (
        <DatasetTableRows
          data={data}
          showButtons={showButtons}
          handleShowOptions={handleShowOptions}
          handleSelectedData={handleSelectedData}
        />
      );
    case "Database Name":
      return (
        <DatabaseTableRows
          data={data}
          showButtons={showButtons}
          handleShowOptions={handleShowOptions}
          handleSelectedData={handleSelectedData}
          handleUpdateDatabase={handleUpdateDatabase}
        />
      );
    case "Experiment Name":
      return (
        <ExperimentTableRows
          data={data}
          showButtons={showButtons}
          handleShowOptions={handleShowOptions}
          handleSelectedData={handleSelectedData}
          refetch={refetch}
        />
      );
    case "Use Cases":
      return (
        <UseCasesTableRows
          data={data}
          showButtons={showButtons}
          handleShowOptions={handleShowOptions}
          handleSelectedData={handleSelectedData}
        />
      );
    default:
      return null;
  }
};

export default CommonTableRows;
