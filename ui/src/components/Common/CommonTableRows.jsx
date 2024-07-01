import React from "react";
import { SlOptionsVertical } from "react-icons/sl";
import { useNavigate } from "react-router-dom";

const CommonTableRows = ({
  data,
  columnHeads,
  showButtons,
  handleShowOptions,
  handleSelectedData,
}) => {
  const navigate = useNavigate();

  const handleRunExperiment = (id) => {
    navigate(`/experiment-result/${id}`);
  };

  return (
    <>
      {columnHeads[0] === "Dataset Name" && (
        <tr className="border-b border-gray-300 ps-2 hover:bg-accent ">
          <td className="text-start text-[13px] py-4 ps-2">{data.name}</td>
          <td className="text-start text-[13px] py-4 ps-2">
            {data.columns.length}
          </td>
          <td className="text-start text-[13px] py-4 ps-2">
            {new Date(data.createdAt).toLocaleDateString()}
          </td>
          <td className="text-start text-[13px] py-4 ps-2">{data.rows}</td>
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
      )}
      {columnHeads[0] === "Database Name" && (
        <tr className="border-b border-gray-300 ps-2 hover:bg-accent">
          <td className="text-start text-[13px] py-4 ps-2">{data.name}</td>
          <td className="text-start text-[13px] py-4 ps-2">
            {data.databaseSetupParams.type}
          </td>
          <td className="text-start text-[13px] py-4 ps-2">
            {data.datasetDetails.datasetName}
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
      )}

      {columnHeads[0] === "Experiment Name" && (
        <tr className="border-b border-gray-300 ps-2 hover:bg-accent cursor-pointer">
          <td
            onClick={() => handleRunExperiment(data?.experimentId.id)}
            data-tip="Click to run the experiment"
            className="w-full text-start text-[13px] py-4 ps-2 tooltip tool"
          >
            {data.name}
          </td>
          <td className="text-start text-[13px] py-4 ps-2">
            {data.databaseConfigName}
          </td>
          <td className="text-start text-[13px] py-4 ps-2">
            {new Date(data.createdAt).toLocaleDateString()}
          </td>
          <td className="text-start text-[13px] py-4 ps-2 flex items-center gap-2">
            {data.experimentState}
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
      )}
    </>
  );
};
export default CommonTableRows;
