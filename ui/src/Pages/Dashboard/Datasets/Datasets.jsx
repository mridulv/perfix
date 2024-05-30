import React, { useState } from "react";
import axios from "axios";
import { useQuery } from "react-query";
import Loading from "../../../components/Loading";
import AddButton from "../../../components/AddButton";
import CommonTable from "../../../components/CommonTable";

const columnHeads = ["Dataset Name", "Number of Columns", "Created At", "Rows"];

// const typeOptions = ["first", "second", "third"];

// [{"text":"as","type":"TextFilter"},{"store":"mysql","type":"DatabaseTypeFilter"},{"name":"da1","type":"DatasetNameFilter"},{"name":"Created","type":"ExperimentStateFilter"}]'
const Datasets = () => {
  const [searchText, setSearchText] = useState("");

  // const handleSetSearchText = (value) => {
  //   setTimeout(() => {
  //     setSearchText(value);
  //   }, 200)
  // }
  const handleSetSearchText = (value) => {
    setSearchText(value)
  }
 console.log(searchText);
  const { data: datasets, isLoading } = useQuery({
    queryKey: ["datasets"],
    queryFn: async () => {
      let values = [];
      if(searchText){
        values = [{text: searchText, type: "TextFilter"}]
      }else{
        values = [];
      }
      const res = await axios.post(`${process.env.REACT_APP_BASE_URL}/dataset`, values, {
        headers: {
          "Content-Type": "application/json"
        },
      });
      const data = await res.data;
      return data;
    },
  });

  const dataForTable = datasets?.map(dataset => ({
    datasetName: dataset.name,
    numberOfColumns: dataset.columns.length,
    createdAt: new Date(dataset.createdAt).toLocaleDateString(), // Format date if necessary
    rows: dataset.rows
  }));


  if (isLoading) return <Loading />;
  return (
    <div className="pt-7 ps-7">
      <div>
        <h3 className="text-2xl font-semibold">Datasets</h3>
      </div>
      <div className="w-[95%] h-[1px] bg-[#fcf8f8] my-6"></div>
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
          <AddButton value={"New Dataset"} link={"/add-dataset"} />
        </div>
      </div>

      <div className="pe-9 ">
        <CommonTable
          data={dataForTable}
          tableHead={"Experiment"}
          columnHeads={columnHeads}
        />
      </div>
    </div>
  );
};

export default Datasets;
