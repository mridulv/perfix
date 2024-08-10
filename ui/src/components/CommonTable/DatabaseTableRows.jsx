/* eslint-disable no-unused-vars */
import React from "react";
import { SlOptionsVertical } from "react-icons/sl";
import { Link } from "react-router-dom";

const DatabaseTableRows = ({ data, showButtons, handleShowOptions, handleSelectedData }) => {
  return (
    <tr className="border-b border-gray-300 ps-2 hover:bg-accent">
      <td className="text-start text-[13px] py-4 ps-2">{data.name}</td>
      <td className="text-start text-[13px] py-4 ps-2">
        {data.databaseSetupParams.type}
      </td>
      <td className="text-start text-[13px] py-4 ps-2 hover:underline hover:text-primary">
        <Link to={`/dataset/${data.datasetDetails.datasetId.id}`}>
          {data.datasetDetails.datasetName}
        </Link>
      </td>
      <td className="text-start text-[13px] py-4 ps-2">
        {new Date(data.createdAt).toLocaleDateString()}
      </td>
      <td className="relative w-[50px]">
        <button
          onClick={() => handleShowOptions(data)}
          className="btn-sm hover:bg-accent rounded"
        >
          <SlOptionsVertical size={13} />
        </button>
        {showButtons === data && (
          <div className="flex flex-col justify-center gap-1 absolute z-10 bottom-[-70px] left-[-15px] bg-white shadow-md rounded p-2 actions">
            <button className="px-2 py-1 text-[13px] hover:bg-accent">
              Edit
            </button>
            <button
              onClick={() => handleSelectedData(data)}
              className="px-2 py-1 text-[13px] hover:bg-accent"
            >
              Delete
            </button>
          </div>
        )}
      </td>
    </tr>
  );
};

export default DatabaseTableRows;
