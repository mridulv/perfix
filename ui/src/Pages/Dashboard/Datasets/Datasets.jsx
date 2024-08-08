/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import Loading from "../../../components/Common/Loading";
import AddButton from "../../../components/Common/AddButton";
import useDatasets from "../../../api/useDatasets";
import AddDatasetModal from "../../../components/Modals/AddDatasetModal";
import CustomSelect from "../../../components/CustomSelect/CustomSelect";
import { useSearchParams } from "react-router-dom";
import CommonTable from "../../../components/CommonTable/CommonTable";

const columnHeads = ["Dataset Name", "Number of Columns", "Created At", "Rows"];

const sampleSelectOptions = [{ option: "True", value: "true" }, { option: "False", value: "false" }];

const Datasets = () => {
  const [open, setOpen] = useState(false);
  const [values, setValues] = useState([]);
  const [sampleFilter, setSampleFilter] = useState({
    option: "Sample Dataset",
    value: "false",
  });

  const [searchParam] = useSearchParams();
  const initialSearchParam = searchParam.get("text") || "";
  const [searchText, setSearchText] = useState(initialSearchParam);

  const handleSetSearchText = (value) => {
    setSearchText(value);
  };

  const handleClearFilters = () => {
    setSearchText("");
    setSampleFilter({
      option: "Sample Dataset",
      value: "false",
    });
  };

  useEffect(() => {
    const newValues = [];
    if (searchText) {
      newValues.push({ text: searchText, type: "TextFilter" });
    }
    const isSampleDataset = sampleFilter.value === "true";
    newValues.push({ isSampleDataset, type: "SampleDatasetFilter" });

    setValues(newValues);
  }, [searchText, sampleFilter]);

  const {
    data: datasets,
    isLoading: datasetsLoading,
    refetch,
  } = useDatasets(values);

  const isFilterActive = searchText || sampleFilter.value !== "false";

  return (
    <div className="pt-7 ps-7">
      <div>
        <h3 className="text-2xl font-semibold -tracking-tighter">Datasets</h3>
      </div>
      <div className="w-[95%] h-[1px] bg-accent my-6"></div>
      <div className="flex justify-between me-9 mt-6 mb-5">
        <div className="flex gap-x-4">
          <input
            className="search-input"
            type="text"
            placeholder="Search"
            value={searchText}
            onChange={(e) => handleSetSearchText(e.target.value)}
          />
          <div>
            <CustomSelect
              options={sampleSelectOptions}
              selected={sampleFilter}
              setSelected={setSampleFilter}
              width={"170px"}
            />
          </div>
          {isFilterActive && (
            <button
              onClick={handleClearFilters}
              className="text-sm font-semibold tracking-wider"
            >
              Clear
            </button>
          )}
        </div>
        <div>
          <AddButton
            value="New Dataset"
            setOpen={() => setOpen(true)}
          ></AddButton>
        </div>
      </div>
      {datasetsLoading ? (
        <Loading />
      ) : (
        <div>
          <>
            <AddDatasetModal
              open={open}
              onClose={() => setOpen(false)}
              datasets={datasets}
              refetch={refetch}
            />
          </>

          <div className="pe-9 ">
            <CommonTable
              data={datasets}
              tableHead={"Dataset"}
              columnHeads={columnHeads}
              refetch={refetch}
              data-testid="datasets-table"
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default Datasets;
