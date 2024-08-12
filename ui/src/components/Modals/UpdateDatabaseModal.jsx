/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import { MdClose } from "react-icons/md";
import toast from "react-hot-toast";
import DatabaseForm from "../AddDatabase/DatabaseForm";
import axiosApi from "../../api/axios";
import useDatabases from "../../api/useDatabases";
import Loading from "../Common/Loading";

const UpdateDatabaseModal = ({ open, onClose, refetch, databaseData }) => {
  
  const [selectedDatasetData, setSelectedDatasetData] = useState(null);
  const { data: databases, isLoading } = useDatabases([]);

  const selectedDatasetId = databaseData.datasetDetails.datasetId.id;

  useEffect(() => {
    if (selectedDatasetId) {
      const fetchDataset = async () => {
        const res = await axiosApi.get(`/dataset/${selectedDatasetId}`);
        const data = await res.data;
        setSelectedDatasetData(data);
      };
      fetchDataset();
    }
  }, [selectedDatasetId, setSelectedDatasetData]);
  //close the modal
  const handleCloseModal = () => {
    onClose();
  };

  //when database updated successfully
  const successFunctionsForDatabase = () => {
    toast.success("Database Updated Successfully");
    refetch();
    onClose();
  };

  if (isLoading) return <Loading />;
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
          w-[80%] lg:w-[35%] bg-white rounded-lg shadow py-6 transition-all
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
        <div className="min-h-[500px]">
          <div className="mb-5 ms-6">
            <p className="text-xl font-semibold">
              Update database {databaseData.name}
            </p>
          </div>
          <div className="w-[95%] h-[1px] bg-accent mb-3 mt-5"></div>
          <div>
            <DatabaseForm
              dataset={selectedDatasetData}
              cancelFunction={handleCloseModal}
              successFunction={successFunctionsForDatabase}
              databases={databases}
              creationFor="updateDatabase"
              isUpdate={true}
              existingDatabase={databaseData}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default UpdateDatabaseModal;
