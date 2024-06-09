import React from "react";
import { SlOptionsVertical } from "react-icons/sl";
import { FaGooglePlay } from "react-icons/fa6";
import RunExperimentsDetails from "./RunExperimentsDetails";

const CommonTableRows = ({
  data,
  columnHeads,
  showButtons,
  showExperimentDetails,
  handleShowOptions,
  handleSelectedData,
  handleRunExperiment,
  dataForRun,
}) => {
  const renderContent = () => {
    switch (columnHeads[0]) {
      case "Dataset Name":
        return (
          <>
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
          </>
        );
      case "Database Name":
        return (
          <>
            <td className="text-start text-[13px] py-4 ps-2">
              {data.name}
            </td>
            <td className="text-start text-[13px] py-4 ps-2">
              {data.storeParams.type}
            </td>
            <td className="text-start text-[13px] py-4 ps-2">
              {data.datasetName}
            </td>
            <td className="text-start text-[13px] py-4 ps-2">{new Date(data.createdAt).toLocaleDateString()}</td>
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
          </>
        );
      case "Experiment Name":
        return (
          <>
            <td className="text-start text-[13px] py-4 ps-2">
              {data.name}
            </td>
            <td className="text-start text-[13px] py-4 ps-2">
              {data.databaseConfigName}
            </td>
            <td className="text-start text-[13px] py-4 ps-2">{new Date(data.createdAt).toLocaleDateString()}</td>
            <td className="text-start text-[13px] py-4 ps-2 flex items-center gap-2">
              {data.experimentState}
            </td>
            <td className="text-start text-[13px] py-4 ps-2">
              <span
                onClick={() => handleRunExperiment(data)}
                className="cursor-pointer hover:bg-gray-50 transition-colors duration-1000 ease-in-out"
              >
                <FaGooglePlay color="green" size={20} />
              </span>
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
          </>
        );
      default:
        return null;
    }
  };

  return (
    <>
      <tr className="border-b border-gray-300 ps-2">
        {renderContent()}
      </tr>

      {columnHeads[0] === "Experiment Name" && (
        <tr>
          <td colSpan={columnHeads.length + 1} className="relative">
            <div
              className={`overflow-hidden transition-all duration-1000 ease-in-out ${
                showExperimentDetails === data ? "max-h-screen" : "max-h-0"
              }`}
            >
              {showExperimentDetails === data && (
                <RunExperimentsDetails data={dataForRun} />
              )}
            </div>
          </td>
        </tr>
      )}
    </>
  );
};

export default CommonTableRows;
