import React, { useState } from "react";
import axios from "axios";
import Loading from "../../../components/Loading";
import CommonTable from "../../../components/CommonTable";
import AddDatasetModal from "../../../components/AddDatasetModal";
import { useQuery } from "react-query";

const columnHeads = ["Dataset Name", "Number of Columns", "Created At", "Rows"];

// [{"text":"as","type":"TextFilter"},{"store":"mysql","type":"DatabaseTypeFilter"},{"name":"da1","type":"DatasetNameFilter"},{"name":"Created","type":"ExperimentStateFilter"}]'
const Datasets = () => {
  // const [datasets, setDatasets] = useState([]);
  // const [datasetsLoading, setDatasetsLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [searchText, setSearchText] = useState("");

  const handleSetSearchText = (value) => {
    setTimeout(() => {
      setSearchText(value);
    }, 500);
  };

  const {
    data: datasets,
    isLoading: datasetsLoading,
    refetch,
  } = useQuery({
    queryKey: ["datasets", searchText],
    queryFn: async () => {
      let values = [];
      if (searchText) {
        values = [{ text: searchText, type: "TextFilter" }];
      }
      const req = await axios.post(
        `${process.env.REACT_APP_BASE_URL}/dataset`,
        values,
        {
          withCredentials: true,
        }
      );
      const data = await req.data;
      return data;
    },
  });


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
          <button
            onClick={() => setOpen(true)}
            className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#57B1FF]"
          >
            Add Dataset
          </button>
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
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default Datasets;
