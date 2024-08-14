/* eslint-disable no-unused-vars */
import React from "react";
import StateIconComponent from "../Common/StateIconComponent";
import axiosApi from "../../api/axios";
import toast from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import { SlOptionsVertical } from "react-icons/sl";

const ExperimentTableRows = ({
  data,
  showButtons,
  handleShowOptions,
  handleSelectedData,
  refetch,
}) => {
  const navigate = useNavigate();

  const handleRunExperiment = async (id) => {
    try {
      const res = await axiosApi.post(`/experiment/${id}/execute`);
      console.log(res);
    } catch (err) {
      console.log(err);
      toast.error(err.message);
    }
  };

  const handleExperimentResult = () => {
    if (data.experimentState === "Completed") {
      navigate(`/experiment/${data.experimentId.id}`);
    }
  };
  return (
    <tr className="border-b border-gray-300 ps-2 hover:bg-accent cursor-pointer">
      <td
        data-tip={
          data.experimentState === "Completed"
            ? "Click to run the experiment"
            : "Please run the experiment first"
        }
        onClick={handleExperimentResult}
        className="w-full text-start text-[13px] py-4 ps-2 tooltip"
      >
        {data.name}
      </td>
      <td className="text-start text-[13px] py-4 ps-2 relative text-primary">
        {data.databaseConfigs.length <= 2 ? (
          data.databaseConfigs.map((config, index) => (
            <span key={config.id || index}>
              {config.databaseConfigName}
              {index < data.databaseConfigs.length - 1 && ", "}
            </span>
          ))
        ) : (
          <>
            {data.databaseConfigs.slice(0, 2).map((config, index) => (
              <span key={config.id || index}>
                {config.databaseConfigName}
                {index < 1 && ", "}
              </span>
            ))}
            <span
              className="text-black cursor-pointer ml-1 relative group underline"
              data-tip={data.databaseConfigs
                .slice(2)
                .map((c) => c.databaseConfigName)
                .join(", ")}
            >
              {` +${data.databaseConfigs.length - 2} more`}
              <span className="text-primary invisible group-hover:visible absolute left-0 top-full mt-1 bg-white border border-gray-200 rounded p-2 shadow-md z-10 whitespace-nowrap">
                {data.databaseConfigs
                  .slice(2)
                  .map((c) => c.databaseConfigName)
                  .join(", ")}
              </span>
            </span>
          </>
        )}
      </td>
      <td className="text-start text-[13px] py-4 ps-2">
        {new Date(data.createdAt).toLocaleDateString()}
      </td>
      <td data-tip={data.experimentState} className="tooltip w-full ps-8">
        <StateIconComponent state={data.experimentState} />
      </td>
      <td className="space-x-3">
        <button
          onClick={() => handleRunExperiment(data?.experimentId.id)}
          className="py-1 px-2 bg-primary text-[13px] text-white rounded-md"
          disabled={data.experimentState === "Completed"}
        >
          Run
        </button>
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
            <button onClick={() => navigate(`/update-experiment/${data.experimentId.id}`)} className="px-2 py-1 text-[13px] hover:bg-accent">
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

export default ExperimentTableRows;
