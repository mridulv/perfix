/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import { FaPlus } from "react-icons/fa6";
import { Link, useNavigate } from "react-router-dom";
import { ImPencil } from "react-icons/im";
import { IoMdClose } from "react-icons/io";
import CustomSelectMultipleOptions from "../CustomSelect/CustomSelectMultipleOptions";
import CommonExperimentFields from "./CommonExperimentFields";
import AddDatabaseModalForExperiment from "../Modals/AddDatabaseModalForExperiment";
import axiosApi from "../../api/axios";
import { handleExperiment } from "../../api/handleExperiment";
import Loading from "../Common/Loading";

const ExperimentForm = ({
  databases = null,
  databasesOptions = null,
  dataset,
  databaseRefetch = null,
  selectedDatabaseCategory,
  experimentData = null,
  isUpdate = false,
}) => {
  const [experimentName, setExperimentName] = useState(
    experimentData ? experimentData.name : ""
  );
  const [selectedDatabases, setSelectedDatabases] = useState(
    experimentData
      ? experimentData.databaseConfigs.map((config) => ({
          value: config.databaseConfigId.id,
          option: `${config.databaseConfigName} (${config.datasetName})`,
        }))
      : []
  );
  const [writeBatchSizeValue, setWriteBatchSizeValue] = useState(
    experimentData ? experimentData.writeBatchSize.toString() : "1000"
  );
  const [concurrentQueries, setConcurrentQueries] = useState(
    experimentData ? experimentData.concurrentQueries.toString() : "100"
  );
  const [openAddDatabaseModal, setOpenAddDatabaseModal] = useState(false);
  const [sqlPlaceholder, setSqlPlaceholder] = useState(
    experimentData?.dbQuery?.sql ? experimentData?.dbQuery?.sql : ""
  );
  const [dbQuery, setDbQuery] = useState(
    experimentData ? experimentData.dbQuery : {}
  );

  const navigate = useNavigate();

  const handleRemoveSelectedDatabase = (databaseToRemove) => {
    if (!experimentData) {
      setSelectedDatabases(
        selectedDatabases.filter((db) => db.value !== databaseToRemove.value)
      );
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (isUpdate) {
      await handleExperiment.handleUpdateExperiment(
        experimentData.experimentId.id,
        experimentName,
        selectedDatabaseCategory.value,
        writeBatchSizeValue,
        concurrentQueries,
        selectedDatabases,
        dbQuery,
        navigate
      );
    } else {
      await handleExperiment.handleAddExperiment(
        experimentName,
        selectedDatabaseCategory.value,
        writeBatchSizeValue,
        concurrentQueries,
        selectedDatabases,
        dbQuery,
        navigate
      );
    }
  };

  useEffect(() => {
    if (
      !experimentData?.dbQuery?.sql &&
      selectedDatabaseCategory.value !== "AWS Cloud - NoSQL Databases"
    ) {
      const fetchSQLPlaceholder = async () => {
        const payload =
          selectedDatabases.length > 0
            ? selectedDatabases.reduce((acc, database) => {
                acc[`databaseConfigId`] = database.value;
                return acc;
              }, {})
            : {};
        try {
          const res = await axiosApi.post(
            "/experiment/sql/placeholder",
            payload
          );
          if (res.status === 200) {
            setSqlPlaceholder(res.data);
          }
        } catch (err) {
          console.log(err);
        }
      };

      fetchSQLPlaceholder();
    }
  }, [selectedDatabases, experimentData, selectedDatabaseCategory]);

  const paramsForInputFieldsComponent = {
    experimentName,
    setExperimentName,
    dataset,
    concurrentQueries,
    setConcurrentQueries,
    writeBatchSizeValue,
    setWriteBatchSizeValue,
    selectedDatabaseCategory,
    sqlPlaceholder,
    dbQuery,
    setDbQuery,
    initialDbQuery: dbQuery,
  };

  console.log(dbQuery);
  return (
    <div>
      <div className="ps-7 mt-6">
        <p className="text-[12px] font-bold mb-[2px] block">Choose Database</p>
        <div className="flex items-center gap-3">
          {selectedDatabases.length > 0 && (
            <div className="min-w-[140px] h-[35px] flex items-center rounded-2xl text-[12px] font-semibold">
              {selectedDatabases?.map((database) => (
                <div
                  key={database.value}
                  className="h-full px-4 mr-2 flex gap-2 items-center justify-center bg-secondary rounded-2xl"
                >
                  <p>{database.option}</p>
                  <ImPencil size={12} />
                  <IoMdClose
                    title="Remove Database"
                    cursor={"pointer"}
                    size={18}
                    onClick={() => handleRemoveSelectedDatabase(database)}
                  />
                </div>
              ))}
            </div>
          )}

          {!experimentData && (
            <>
              <div>
                <CustomSelectMultipleOptions
                  selected={selectedDatabases}
                  setSelected={setSelectedDatabases}
                  options={databasesOptions}
                  width="w-[200px]"
                  name="Databases"
                />
              </div>
              <div>
                <button
                  onClick={() => setOpenAddDatabaseModal(true)}
                  className="px-2 flex items-center gap-2 text-primary text-[12px] rounded font-semibold"
                >
                  <FaPlus />
                  Add new database
                </button>
              </div>
            </>
          )}

          <AddDatabaseModalForExperiment
            open={openAddDatabaseModal}
            onClose={() => setOpenAddDatabaseModal(false)}
            databases={databases}
            selectedDataset={dataset}
            refetch={databaseRefetch}
          />
        </div>
      </div>

      <div className="ps-7 mt-7">
        <form onSubmit={handleSubmit}>
           
            <CommonExperimentFields params={paramsForInputFieldsComponent} />
          

          <div className="mt-[150px] pb-3 flex gap-2">
            <button
              className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#6b3b51d2]"
              type="submit"
            >
              {experimentData ? "Update" : "Launch"}
            </button>
            <Link
              to={"/experiment"}
              className="px-3 py-1 text-[14px] font-bold border-2 border-gray-300 rounded"
            >
              Cancel
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ExperimentForm;
