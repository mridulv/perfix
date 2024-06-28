import React, { useEffect, useState } from "react";
import Loading from "../../../components/Common/Loading";
import useExperiments from "../../../api/useExperiment";
import useDatasets from "../../../api/useDatasets";
import useDatabases from "../../../api/useDatabases";
import AddExperimentModal from "../../../components/Modals/AddExperimentModal";
import CustomSelect from "../../../components/CustomSelect/CustomSelect";
import CommonTable from "../../../components/Common/CommonTable";
import { Filters } from "../../../hooks/filters";
import { useNavigate } from "react-router-dom";

const columnHeads = [
  "Experiment Name",
  "DatabaseConfig Name",
  "Created At",
  "Experiment State",
];

const Experiment = () => {
  const [open, setOpen] = useState(false);
  const [searchText, setSearchText] = useState("");
  const [values, setValues] = useState([]);

  const {
    selectedDatabaseType,
    setSelectedDatabaseType,
    databaseTypesOptions,
    selectExperimentState,
    setSelectExperimentState,
    selectedDatasetName,
    setSelectedDatasetName,
    experimentStateOptions,
  } = Filters();

  const navigate = useNavigate();

  const handleSearchText = (e) => {
    setSearchText(e.target.value);
  };

  useEffect(() => {
    const newValues = [];
    if (searchText) {
      newValues.push({ text: searchText, type: "TextFilter" });
    }
    if (selectedDatabaseType.value !== "choose") {
      newValues.push({
        store: selectedDatabaseType.value,
        type: "DatabaseTypeFilter",
      });
    }
    if (selectedDatasetName.value !== "choose") {
      newValues.push({
        name: selectedDatasetName.value,
        type: "DatasetNameFilter",
      });
    }
    setValues(newValues);
  }, [searchText, selectedDatabaseType, selectedDatasetName]);

  
  const { data: experiments, isLoading: experimentsLoading } =
    useExperiments(values);
  const { data: datasets, isLoading: datasetsLoading } = useDatasets([]);
  const {
    data: databases,
    isLoading: databasesLoading,
    refetch: databaseRefetch,
  } = useDatabases([]);

  const datasetNameOptions = datasets?.map((dataset) => ({
    option: dataset.name,
    value: dataset.name,
  }));

  return (
    <div className="">
      <div className="pt-7 ps-7">
        <h3 className="text-2xl font-semibold">Experiments</h3>
      </div>
      <div className="w-[95%] h-[1px] bg-accent my-6"></div>
      <div className="mb-3 ps-7 pe-9 flex justify-between">
        <div className="flex gap-x-4">
          <input
            className="w-[200px] p-1  border-2 border-gray-200 rounded search-input"
            type="text"
            name=""
            id=""
            placeholder="Search"
            onChange={(e) => handleSearchText(e)}
          />

          <CustomSelect
            selected={selectedDatabaseType}
            setSelected={setSelectedDatabaseType}
            options={databaseTypesOptions}
            width="w-[200px]"
          />
          <CustomSelect
            selected={selectedDatasetName}
            setSelected={setSelectedDatasetName}
            options={datasetNameOptions}
            width="w-[200px]"
          />
          <CustomSelect
            selected={selectExperimentState}
            setSelected={setSelectExperimentState}
            options={experimentStateOptions}
            width="w-[200px]"
          />
        </div>
        <div>
          <button
            onClick={() => navigate("/add-experiment")}
            className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#57B1FF]"
          >
            New Experiment
          </button>
        </div>
      </div>
      <AddExperimentModal
        open={open}
        onClose={() => setOpen(false)}
        experiments={experiments}
        datasets={datasets}
        databases={databases}
        databaseRefetch={databaseRefetch}
      />

      {experimentsLoading && databasesLoading && datasetsLoading ? (
        <Loading />
      ) : (
        <div className="ps-7 pe-9 ">
          <CommonTable
            data={experiments}
            tableHead={"Experiment"}
            columnHeads={columnHeads}
          />
        </div>
      )}
    </div>
  );
};

export default Experiment;
