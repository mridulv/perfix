import React, { useEffect, useState } from "react";
import Loading from "../../../components/Common/Loading";
import { MdClose } from "react-icons/md";
import useDatabases from "../../../api/useDatabases";
import useDatasets from "../../../api/useDatasets";
import CustomSelect from "../../../components/CustomSelect/CustomSelect";
import AddButton from "../../../components/Common/AddButton";
import AddDatabaseModal from "../../../components/Modals/AddDatabaseModal";
import CommonTable from "../../../components/Common/CommonTable";
import { Filters } from "../../../hooks/filters";

const columnHeads = [
  "Database Name",
  "Database Type",
  "Dataset Name",
  "Created At",
];

const DBConfiguration = () => {
  const [open, setOpen] = useState(false);
  const [searchText, setSearchText] = useState("");
  const [values, setValues] = useState([]);

  const {
    databaseTypesOptions,
    selectedDatabaseType,
    setSelectedDatabaseType,
    selectedDatasetName,
    setSelectedDatasetName,
  } = Filters();

  const handleSearchText = (e) => {
    setSearchText(e.target.value);
  };

  // if (searchText) {
  //   values = [...values, { text: searchText, type: "TextFilter" }];
  // }
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

  const { data: databases, isLoading, refetch } = useDatabases(values);
  const { data: datasets, isLoading: datasetsLoading } = useDatasets([]);

  useEffect(() => {
    refetch();
  }, [values, refetch]);

  const datasetNamesOptions = datasets?.map((dataset) => ({
    option: dataset.name,
    value: dataset.name,
  }));

  // for clearing the filter
  const handleClearDatabaseFilter = () => {
    setSelectedDatabaseType({
      option: "Choose Database",
      value: "choose",
    });
    setSelectedDatasetName({
      option: "Choose Dataset",
      value: "choose",
    });
  };
  
  return (
    <div className="ps-7 pt-7">
      <div>
        <h3 className="text-2xl font-semibold">Database</h3>
      </div>
      <div className="w-[95%] h-[1px] bg-accent my-6"></div>
      <div className="flex justify-between me-9 mt-6 mb-3">
        <div className="flex gap-x-4">
          <input
            className="w-[200px] px-1 py-[6px]  border-2 border-gray-200 rounded search-input"
            type="text"
            name=""
            id=""
            placeholder="Search"
            onChange={(e) => handleSearchText(e)}
          />
          <div className="ms-3">
            <CustomSelect
              selected={selectedDatabaseType}
              setSelected={setSelectedDatabaseType}
              options={databaseTypesOptions}
              width="w-[180px]"
            />
          </div>
          <div>
            {databases && (
              <CustomSelect
                selected={selectedDatasetName}
                setSelected={setSelectedDatasetName}
                options={datasetNamesOptions}
                width="w-[180px]"
              />
            )}
          </div>

          {(selectedDatabaseType.value !== "choose" ||
            selectedDatasetName.value !== "choose") && (
            <div className="">
              <button
                className="w-[40px] h-[40px] bg-secondary flex items-center justify-center rounded-md"
                onClick={handleClearDatabaseFilter}
              >
                <MdClose size={20} />
              </button>
            </div>
          )}
        </div>
        <div>
          <AddButton
            value="New Database"
            setOpen={() => setOpen(true)}
          ></AddButton>
        </div>
      </div>
      {isLoading && datasetsLoading ? (
        <Loading />
      ) : (
        <div>
          <AddDatabaseModal
            open={open}
            onClose={() => setOpen(false)}
            datasets={datasets}
            refetch={refetch}
            databases={databases}
          />
          <div className="me-9">
            <div className="">
              <CommonTable
                data={databases}
                tableHead={"Database"}
                columnHeads={columnHeads}
                refetch={refetch}
              />
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DBConfiguration;
