import React, { useEffect, useState } from "react";
import DeleteModal from "./DeleteModal";
import toast from "react-hot-toast";
import CommonTableRows from "./CommonTableRows";

const CommonTable = ({
  data,
  tableHead,
  columnHeads,
  dataForRun = null,
  refetch,
}) => {
  const [showButtons, setShowButtons] = useState(null);
  const [showExperimentDetails, setShowExperimentDetails] = useState(null);
  const [selectedData, setSelectedData] = useState(null);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);

  //showing the action buttons
  const handleShowOptions = (data) => {
    setShowButtons(showButtons === data ? null : data);
  };

  //for showing the experiment results
  const handleRunExperiment = (experiment) => {
    setShowExperimentDetails(
      showExperimentDetails === experiment ? null : experiment
    );
  };

  //for the delete modal
  const handleSelectedData = (data) => {
    setOpenDeleteModal(true);
    setSelectedData(selectedData === data ? null : data);
  };

  // for the actions buttons
  const handleClickOutside = (event) => {
    const optionsButton = event.target.closest(".btn-sm");
    const actions = event.target.closest(".actions");

    if (!optionsButton && !actions) {
      setShowButtons(null);
    }
  };

  useEffect(() => {
    document.addEventListener("click", handleClickOutside);
    return () => {
      document.removeEventListener("click", handleClickOutside);
    };
  }, []);

  const deleteUrl =
    columnHeads[0] === "Dataset Name"
      ? `${process.env.REACT_APP_BASE_URL}/dataset`
      : columnHeads[0] === "Database Name"
      ? `${process.env.REACT_APP_BASE_URL}/config`
      : `${process.env.REACT_APP_BASE_URL}/experiment`;

  const handleDatasetDeleteSuccess = () => {
    refetch();
    toast.success("Dataset Deleted Successfully!");
    setSelectedData(null);
    setOpenDeleteModal(false);
  };

  const handleDatabaseDeleteSuccess = () => {
    // ... (handle database deletion success)
    refetch();
    toast.success("Database Deleted Successfully!");
    setSelectedData(null);
    setOpenDeleteModal(false);
  };

  const handleExperimentDeleteSuccess = () => {
    // ... (handle experiment deletion success)
    refetch();
    toast.success("Experiment Deleted Successfully!");
    setSelectedData(null);
    setOpenDeleteModal(false);
  };

  const successFunctions =
    columnHeads[0] === "Dataset Name"
      ? handleDatasetDeleteSuccess
      : columnHeads[0] === "Database Name"
      ? handleDatabaseDeleteSuccess
      : handleExperimentDeleteSuccess;

  const dataId =
    columnHeads[0] === "Dataset Name"
      ? selectedData?.id?.id
      : columnHeads[0] === "Database Name"
      ? selectedData?.databaseConfigId.id
      : selectedData?.experimentId.id;
  return (
    <div>
      <div className="bg-accent py-2 ps-3 text-[14px] font-semibold border-b-2 border-gray-300 rounded-t-lg">
        <p>
          {data?.length || 0} {tableHead}
        </p>
      </div>
      <table className="w-full ">
        <thead className="">
          <tr className="border-b border-gray-300">
            <th className="text-start w-[45%] text-[12px] text-gray-300 py-4 ps-2">
              {columnHeads[0]}
            </th>
            {columnHeads?.slice(1, columnHeads.length).map((head, i) => (
              <th key={i} className="text-start text-[12px] text-gray-300 ps-2">
                {head}
              </th>
            ))}
            <th></th>
          </tr>
        </thead>
        <tbody>
          {data ? (
            data.map((d, i) => (
              <CommonTableRows
                key={i}
                data={d}
                columnHeads={columnHeads}
                showButtons={showButtons}
                showExperimentDetails={showExperimentDetails}
                handleShowOptions={handleShowOptions}
                handleSelectedData={handleSelectedData}
                handleRunExperiment={handleRunExperiment}
                dataForRun={dataForRun}
              />
            ))
          ) : (
            <tr className="text-start text-[13px] py-4 ps-2">
              <td>You haven't added any data</td>
            </tr>
          )}
        </tbody>
      </table>
      <DeleteModal
        open={openDeleteModal}
        onClose={() => setOpenDeleteModal(false)}
        dataId={dataId}
        actionHead={`Delete ${tableHead}?`}
        actionText={`Once you delete the ${tableHead.toLowerCase()}, you cannot modify existing experiments with this dataset, however you can create new datasets. Are you sure you want to continue?`}
        deleteUrl={deleteUrl}
        successFunctions={successFunctions}
      />
    </div>
  );
};

export default CommonTable;
