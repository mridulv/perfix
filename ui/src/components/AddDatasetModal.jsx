import React, { useState } from "react";
import { MdClose } from "react-icons/md";
import AddDataset from "./AddDataset";
import { useNavigate } from "react-router-dom";
import { handleAddDatasetApi } from "../utilities/api";

const AddDatasetModal = ({ open, onClose, datasets }) => {
  const [columns, setColumns] = useState([{ columnName: "", columnType: "" }]);

  const navigate = useNavigate();

  

  const handleAddColumn = () => {
    setColumns([...columns, { columnName: "", columnType: "" }]);
  };

  const handleCloseModal = () => {
    onClose();
    setColumns([{ columnName: "", columnType: "" }]);
  };

  const handleAddDataset = async (event) => {
    event.preventDefault();
    const navigateFor = "datasetPage";
    handleAddDatasetApi(
      event,
      datasets,
      columns,
      setColumns,
      navigate,
      navigateFor
    );
  };

  return (
    <div
      onClick={onClose}
      className={`
        fixed inset-0 flex justify-center items-center transition-colors
        ${open ? "visible bg-black/20" : "invisible"} z-50 
      `}
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className={`
          bg-white rounded-lg shadow p-6 transition-all
          ${open ? "scale-100 opacity-100" : "scale-125 opacity-0"}
        `}
        style={{ maxHeight: "80vh", overflow: "auto" }}
      >
        <button
          onClick={handleCloseModal}
          className="absolute top-5 right-2 p-1 rounded-lg text-gray-400 bg-white hover:bg-gray-50 hover:text-gray-600"
        >
          <MdClose size={25} />
        </button>
        <div className="">
          <h2 className="ms-2 text-[20px] font-bold">Create new dataset</h2>
          <div className="w-[95%] h-[1px] bg-accent mb-3 mt-5"></div>
          <form onSubmit={handleAddDataset} className="mb-2 ps-3 pe-8 py-2 ">
            <AddDataset columns={columns} handleAddColumn={handleAddColumn} />
            <div className="mt-[50px] flex gap-3 pb-4">
              <button
                className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#6b3b51d2]"
                type="submit"
              >
                Add
              </button>
              <button
                onClick={handleCloseModal}
                className="px-3 py-1 text-[14px] font-bold border-2 border-gray-300 rounded"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AddDatasetModal;
