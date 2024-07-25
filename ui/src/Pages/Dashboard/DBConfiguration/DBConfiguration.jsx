import { useEffect, useState } from "react";
import Loading from "../../../components/Common/Loading";
import useDatabases from "../../../api/useDatabases";
import useDatasets from "../../../api/useDatasets";
import CustomSelect from "../../../components/CustomSelect/CustomSelect";
import AddButton from "../../../components/Common/AddButton";
import AddDatabaseModal from "../../../components/Modals/AddDatabaseModal";
import CommonTable from "../../../components/Common/CommonTable";
import { Filters } from "../../../hooks/filters";
import { useSearchParams } from "react-router-dom";

const columnHeads = [
  "Database Name",
  "Database Type",
  "Dataset Name",
  "Created At",
];

const DBConfiguration = () => {
  const [open, setOpen] = useState(false);
  const [values, setValues] = useState([]);

  const {
    databaseTypesOptions,
    selectedDatabaseType,
    setSelectedDatabaseType,
    selectedDatasetName,
    setSelectedDatasetName,
  } = Filters();

  const [searchParam] = useSearchParams();
  const initialSearchParam = searchParam.get("text") || "";
  const [searchText, setSearchText] = useState(initialSearchParam);

  const handleSearchText = (e) => {
    setSearchText(e.target.value);
  };

  // Clear filter function
  const handleClearFilters = () => {
    setSearchText("");
    setSelectedDatabaseType({
      option: "Databases",
      value: "choose",
    });
    setSelectedDatasetName({
      option: "Datasets",
      value: "choose",
    });
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

  const { data: databases, isLoading, refetch } = useDatabases(values);
  const { data: datasets, isLoading: datasetsLoading } = useDatasets([]);

  useEffect(() => {
    refetch();
  }, [values, refetch]);

  const datasetNamesOptions = datasets?.map((dataset) => ({
    option: dataset.name,
    value: dataset.name,
  }));

  const isFilterActive =
    searchText ||
    selectedDatabaseType.value !== "choose" ||
    selectedDatasetName.value !== "choose";

  return (
    <div className="ps-7 pt-7">
      <div>
        <h3 className="text-2xl font-semibold -tracking-tighter">Database</h3>
      </div>
      <div className="w-[95%] h-[1px] bg-accent my-6"></div>
      <div className="flex justify-between me-9 mt-6 mb-5">
        <div className="flex gap-x-4">
          <input
            className="search-input"
            type="text"
            placeholder="Search"
            value={searchText}
            onChange={(e) => handleSearchText(e)}
          />
          <div className="">
            <CustomSelect
              selected={selectedDatabaseType}
              setSelected={setSelectedDatabaseType}
              options={databaseTypesOptions}
              width="w-[100px]"
            />
          </div>
          <div>
            {databases && (
              <CustomSelect
                selected={selectedDatasetName}
                setSelected={setSelectedDatasetName}
                options={datasetNamesOptions}
                width="w-[90px]"
              />
            )}
          </div>

          {isFilterActive && (
            <div className="">
              <button
                onClick={handleClearFilters}
                className="text-sm font-semibold tracking-wider"
              >
                Clear
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
