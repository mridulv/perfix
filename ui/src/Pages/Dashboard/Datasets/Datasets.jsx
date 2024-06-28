import React, { useEffect, useState } from "react";
import Loading from "../../../components/Common/Loading";
import AddButton from "../../../components/Common/AddButton";
import useDatasets from "../../../api/useDatasets";
import CommonTable from "../../../components/Common/CommonTable";
import AddDatasetModal from "../../../components/Modals/AddDatasetModal";

const columnHeads = ["Dataset Name", "Number of Columns", "Created At", "Rows"];

// [{"text":"as","type":"TextFilter"},{"store":"mysql","type":"DatabaseTypeFilter"},{"name":"da1","type":"DatasetNameFilter"},{"name":"Created","type":"ExperimentStateFilter"}]'
const Datasets = () => {
  // const [datasets, setDatasets] = useState([]);
  // const [datasetsLoading, setDatasetsLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [searchText, setSearchText] = useState("");
  const [values, setValues] = useState([])

  const handleSetSearchText = (value) => {
    setSearchText(value)
  };


  useEffect(() => {
    const newValues = [];
    if (searchText) {
      newValues.push({ text: searchText, type: "TextFilter" });
    }
    setValues(newValues)
  }, [searchText])
  
  const {data: datasets, isLoading: datasetsLoading, refetch} = useDatasets(values)

  // Add this useEffect hook to re-call useDatasets when searchText changes
  useEffect(() => {
    refetch();
  }, [values, refetch]);

  return (
    <div className="pt-7 ps-7">
      <div>
        <h3 className="text-2xl font-semibold">Datasets</h3>
      </div>
      <div className="w-[95%] h-[1px] bg-accent my-6"></div>
      <div className="flex justify-between me-9 mt-6 mb-5">
        <div className="flex gap-x-4">
          <input
            className="w-[200px] px-1 py-[6px]  border-2 border-gray-200 rounded search-input"
            type="text"
            name=""
            id=""
            placeholder="Search"
            onChange={(e) => handleSetSearchText(e.target.value)}
          />
        </div>
        <div>
          <AddButton value="New Dataset" setOpen={() => setOpen(true)}></AddButton>
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
