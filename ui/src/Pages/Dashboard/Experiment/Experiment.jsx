/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import Loading from "../../../components/Common/Loading";
import useExperiments from "../../../api/useExperiment";
import useDatasets from "../../../api/useDatasets";
import useDatabases from "../../../api/useDatabases";
import AddExperimentModal from "../../../components/Modals/AddExperimentModal";
import CustomSelect from "../../../components/CustomSelect/CustomSelect";
import CommonTable from "../../../components/Common/CommonTable";
import { Filters } from "../../../hooks/filters";
import { useNavigate, useSearchParams } from "react-router-dom";
import MoreFilter from "../../../components/CustomSelect/MoreFilter";

const columnHeads = [
  "Experiment Name",
  "DatabaseConfig Name",
  "Created At",
  "Experiment State",
];

const Experiment = () => {
  const [open, setOpen] = useState(false);
  const [filterValues, setFilterValues] = useState([]);
  const [selectedOptions, setSelectedOptions] = useState({
    States: null,
    Database: null,
  });

  const {
    selectedDatabaseType,
    setSelectedDatabaseType,
    databaseTypesOptions,
    selectedDatasetName,
    setSelectedDatasetName,
  } = Filters();
  const navigate = useNavigate();

  const handleSelect = (category, item) => {
    setSelectedOptions((prev) => ({
      ...prev,
      [category]: item,
    }));
  };

  const [searchParams] = useSearchParams();
  const initialSearchText = searchParams.get("text") || "";
  const [searchText, setSearchText] = useState(initialSearchText);

  const handleSearchText = (e) => {
    setSearchText(e.target.value);
  };

  const handleClearFilters = () => {
    setSearchText("");
    setSelectedDatabaseType({option: "Databases", value: "choose" });
    setSelectedDatasetName({option: "Datasets", value: "choose" });
    setSelectedOptions({
      States: null,
      Database: null,
    });
  };

  useEffect(() => {
    const newFilterValues = [];

    if (searchText) {
      newFilterValues.push({ text: searchText, type: "TextFilter" });
    }
    if (selectedDatabaseType.value !== "choose") {
      newFilterValues.push({ store: selectedDatabaseType.value, type: "DatabaseTypeFilter" });
    }
    if (selectedDatasetName.value !== "choose") {
      newFilterValues.push({ name: selectedDatasetName.value, type: "DatasetNameFilter" });
    }
    if (selectedOptions.States) {
      newFilterValues.push({ name: selectedOptions.States, type: "ExperimentStateFilter" });
    }

    setFilterValues(newFilterValues);
  }, [searchText, selectedDatabaseType, selectedDatasetName, selectedOptions]);

  const { data: experiments, isLoading: experimentsLoading, refetch } = useExperiments(filterValues);
  const { data: datasets, isLoading: datasetsLoading } = useDatasets([]);
  const { data: databases, isLoading: databasesLoading, refetch: databaseRefetch } = useDatabases([]);

  const datasetNameOptions = datasets?.map((dataset) => ({
    option: dataset.name,
    value: dataset.name,
  }));

  const isFilterActive =
    searchText ||
    selectedDatabaseType.value !== "choose" ||
    selectedDatasetName.value !== "choose" ||
    selectedOptions.States;

  return (
    <div className="">
      <div className="pt-7 ps-7">
        <h3 className="text-2xl font-semibold -tracking-tighter">Experiments</h3>
      </div>
      <div className="w-[95%] h-[1px] bg-accent my-6"></div>
      <div className="mb-5 ps-7 pe-9 flex justify-between">
        <div className="flex gap-x-4">
          <input
            className=" search-input"
            type="text"
            placeholder="Search"
            onChange={handleSearchText}
            value={searchText}
          />
          <CustomSelect
            selected={selectedDatabaseType}
            setSelected={setSelectedDatabaseType}
            options={databaseTypesOptions}
            width="w-[100px]"
          />
          <CustomSelect
            selected={selectedDatasetName}
            setSelected={setSelectedDatasetName}
            options={datasetNameOptions}
            width="w-[90px]"
          />
          <MoreFilter onSelect={handleSelect} selectedOptions={selectedOptions} />
          {isFilterActive && (
            <button
              onClick={handleClearFilters}
              className="text-sm font-semibold tracking-wider"
            >
              Clear
            </button>
          )}
        </div>
        <button
          onClick={() => navigate("/add-experiment")}
          className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#57B1FF]"
        >
          New Experiment
        </button>
      </div>
      <AddExperimentModal
        open={open}
        onClose={() => setOpen(false)}
        experiments={experiments}
        datasets={datasets}
        databases={databases}
        databaseRefetch={databaseRefetch}
      />
      {experimentsLoading || databasesLoading || datasetsLoading ? (
        <Loading />
      ) : (
        <div className="ps-7 pe-9">
          <CommonTable
            data={experiments}
            tableHead={"Experiment"}
            columnHeads={columnHeads}
            refetch={refetch}
          />
        </div>
      )}
    </div>
  );
};

export default Experiment;
