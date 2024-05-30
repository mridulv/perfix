import React, { useEffect, useState } from "react";
import { SlOptionsVertical } from "react-icons/sl";
import { FaGooglePlay } from "react-icons/fa6";
import RunExperimentsDetails from "./RunExperimentsDetails";
import DeleteModal from "./DeleteModal";
import axios from "axios";

const CommonTable = ({ data, tableHead, columnHeads, dataForRun = null }) => {
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

  const handleDelete = async (id) => {
    try {
      const value = [];
      const res = await axios.delete(
        `${process.env.REACT_APP_BASE_URL}/dataset/${id}`,
        value,
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );
      console.log(res);
    } catch (error) {
      console.error("Error deleting dataset:", error);
    }
  };

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
          {data &&
            columnHeads[0] === "Dataset Name" &&
            data.map((d, i) => (
              <tr key={i} className="border-b border-gray-300 ps-2">
                <td className="text-start text-[13px] py-4 ps-2">{d.name}</td>
                <td className="text-start text-[13px] py-4 ps-2">
                  {d.columns.length}
                </td>
                <td className="text-start text-[13px] py-4 ps-2">
                  {new Date(d.createdAt).toLocaleDateString()}
                </td>
                <td className="text-start text-[13px] py-4 ps-2">{d.rows}</td>
                <td className="relative w-[50px]">
                  <button
                    onClick={() => handleShowOptions(d)}
                    className="btn-sm hover:bg-accent rounded"
                  >
                    <SlOptionsVertical size={13} />
                  </button>
                  {showButtons === d && (
                    <div className="flex flex-col justify-center gap-1 absolute z-10 bottom-[-70px] left-[-15px] bg-white shadow-md rounded p-2 actions">
                      <button className="px-2 py-1 text-[13px] hover:bg-accent">
                        Edit
                      </button>
                      <button
                        onClick={() => handleSelectedData(d)}
                        className="px-2 py-1 text-[13px] hover:bg-accent"
                      >
                        Delete
                      </button>
                    </div>
                  )}
                </td>
              </tr>
            ))}

          {data &&
            columnHeads[0] === "Database Name" &&
            data.map((d, i) => (
              <tr key={i} className="border-b border-gray-300 ps-2">
                <td className="text-start text-[13px] py-4 ps-2">
                  {d.databaseName}
                </td>
                <td className="text-start text-[13px] py-4 ps-2">
                  {d.databaseType}
                </td>
                <td className="text-start text-[13px] py-4 ps-2">
                  {d.datasetName}
                </td>
                <td className="text-start text-[13px] py-4 ps-2">
                  {d.createdAt}
                </td>
                <td className="relative w-[50px]">
                  <button
                    onClick={() => handleShowOptions(d)}
                    className="btn-sm hover:bg-accent rounded"
                  >
                    <SlOptionsVertical size={13} />
                  </button>
                  {showButtons === d && (
                    <div className="flex flex-col justify-center gap-1 absolute z-10 bottom-[-70px] left-[-15px] bg-white shadow-md rounded p-2 actions">
                      <button className="px-2 py-1 text-[13px] hover:bg-accent">
                        Edit
                      </button>
                      <button className="px-2 py-1 text-[13px] hover:bg-accent">
                        Delete
                      </button>
                    </div>
                  )}
                </td>
              </tr>
            ))}
          {data &&
            columnHeads[0] === "Experiment Name" &&
            data.map((d, i) => (
              <React.Fragment key={i}>
                <tr className="border-b border-gray-300 ps-2 ">
                  <td className="text-start text-[13px] py-4 ps-2">
                    {d.experimentName}
                  </td>
                  <td className="text-start text-[13px] py-4 ps-2">
                    {d.databaseConfigName}
                  </td>
                  <td className="text-start text-[13px] py-4 ps-2">
                    {d.createdAt}
                  </td>
                  <td className="text-start text-[13px] py-4 ps-2 flex items-center gap-2">
                    {d.experimentState}
                  </td>
                  <td className="text-start text-[13px] py-4 ps-2">
                    <span
                      onClick={() => handleRunExperiment(d)}
                      className="cursor-pointer hover:bg-gray-50 transition-colors duration-1000 ease-in-out"
                    >
                      <FaGooglePlay color="green" size={20} />
                    </span>
                  </td>
                  <td className="relative w-[50px]">
                    <button
                      onClick={() => handleShowOptions(d)}
                      className="btn-sm hover:bg-accent rounded"
                    >
                      <SlOptionsVertical size={13} />
                    </button>
                    {showButtons === d && (
                      <div className="flex flex-col justify-center gap-1 absolute z-10 bottom-[-70px] left-[-15px] bg-white shadow-md rounded p-2 actions">
                        <button className="px-2 py-1 text-[13px] hover:bg-accent">
                          Edit
                        </button>
                        <button className="px-2 py-1 text-[13px] hover:bg-accent">
                          Delete
                        </button>
                      </div>
                    )}
                  </td>
                </tr>

                <tr>
                  <td colSpan={columnHeads.length + 1} className="relative">
                    <div
                      className={`overflow-hidden transition-all duration-1000 ease-in-out ${
                        showExperimentDetails === d ? "max-h-screen" : "max-h-0"
                      }`}
                    >
                      {showExperimentDetails === d && (
                        <RunExperimentsDetails data={dataForRun} />
                      )}
                    </div>
                  </td>
                </tr>
              </React.Fragment>
            ))}

          {!data && (
            <tr className="text-start text-[13px] py-4 ps-2">
              <td>You haven't added any data</td>
            </tr>
          )}
        </tbody>
      </table>
      <DeleteModal
        open={openDeleteModal}
        onClose={() => setOpenDeleteModal(false)}
        data={selectedData}
        action={handleDelete}
        actionHead={"Delete dataset?"}
        actionText={
          "Once you delete the dataset, you cannot modify existing experiments with this dataset, however you can create new datasets. Are you sure you want to continue?"
        }
      />
    </div>
  );
};

export default CommonTable;
