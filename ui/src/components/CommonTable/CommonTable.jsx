/* eslint-disable react/no-unescaped-entities */
/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import DeleteModal from "../Modals/DeleteModal";
import toast from "react-hot-toast";
import CommonTableRows from "./CommonTableRows";

const CommonTable = ({ data, tableHead, columnHeads, refetch }) => {
  const [showButtons, setShowButtons] = useState(null);
  const [selectedData, setSelectedData] = useState(null);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);

  const handleShowOptions = (data) => setShowButtons(prevData => prevData === data ? null : data);
  const handleSelectedData = (data) => {
    setOpenDeleteModal(true);
    setSelectedData(prevData => prevData === data ? null : data);
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (!event.target.closest(".btn-sm") && !event.target.closest(".actions")) {
        setShowButtons(null);
      }
    };
    document.addEventListener("click", handleClickOutside);
    return () => document.removeEventListener("click", handleClickOutside);
  }, []);

  const getDeleteUrl = () => {
    const baseUrl = `${import.meta.env.VITE_BASE_URL}`;
    const endpoints = {
      "Dataset Name": "dataset",
      "Database Name": "config",
      "Use Cases": "usecases",
      "Experiment Name": "experiment"
    };
    return `${baseUrl}/${endpoints[columnHeads[0]] || "experiment"}`;
  };

  const handleDeleteSuccess = (entityType) => {
    refetch();
    toast.success(`${entityType} Deleted Successfully!`);
    setSelectedData(null);
    setOpenDeleteModal(false);
  };

  const successFunctions = () => handleDeleteSuccess(tableHead);

  const getDataId = (selectedData) => {
    if (!selectedData) return undefined;

    switch (columnHeads[0]) {
      case "Dataset Name":
        return selectedData.id?.id;
      case "Database Name":
        return selectedData.databaseConfigId?.id;
      case "Use Cases":
        return selectedData.useCaseId?.id;
      case "Experiment Name":
        return selectedData.experimentId?.id;
      default:
        return undefined;
    }
  };

  return (
    <div>
      <div className="bg-accent py-2 ps-3 text-[14px] font-semibold border-b-2 border-gray-300 rounded-t-lg">
        <p>{data?.length || 0} {tableHead}</p>
      </div>
      <table className="w-full">
        <thead>
          <tr className="border-b border-gray-300">
            {columnHeads.map((head, i) => (
              <th key={i} className={`text-start text-[12px] text-gray-300 py-4 ps-2 ${i === 0 ? 'w-[45%]' : ''}`}>
                {head}
              </th>
            ))}
            <th></th>
          </tr>
        </thead>
        <tbody>
          {data?.map((d, i) => (
            <CommonTableRows
              key={i}
              data={d}
              columnHeads={columnHeads}
              showButtons={showButtons}
              handleShowOptions={handleShowOptions}
              handleSelectedData={handleSelectedData}
            />
          )) || (
            <tr className="text-start text-[13px] py-4 ps-2">
              <td>You haven't added any data</td>
            </tr>
          )}
        </tbody>
      </table>
      <DeleteModal
        open={openDeleteModal}
        onClose={() => setOpenDeleteModal(false)}
        dataId={getDataId(selectedData)}
        actionHead={`Delete ${tableHead}?`}
        actionText={`Once you delete the ${tableHead.toLowerCase()}, you cannot modify existing experiments with this dataset, however you can create new datasets. Are you sure you want to continue?`}
        deleteUrl={getDeleteUrl()}
        successFunctions={successFunctions}
      />
    </div>
  );
};

export default CommonTable;