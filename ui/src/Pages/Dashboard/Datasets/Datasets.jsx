import React, { useState } from "react";
import axios from "axios";
import { FaPlus } from "react-icons/fa6";
import { useQuery } from "react-query";
import toast from "react-hot-toast";
import Loading from "../../../components/Loading";
import AddButton from "../../../components/AddButton";
import CommonTable from "../../../components/CommonTable";

const options = [
  { value: "us", label: "United States" },
  { value: "ca", label: "Canada" },
  { value: "mx", label: "Mexico" },
];

const Datasets = () => {
  const {
    data: datasets,
    isLoading,
    refetch,
  } = useQuery({
    queryKey: ["datasets"],
    queryFn: async () => {
      const res = await axios.get(`${process.env.REACT_APP_BASE_URL}/dataset`);
      const data = await res.data;
      console.log(data);
      return data;
    },
  });

  if (isLoading) return <Loading />;
  return (
    <div className="pt-7 ps-7">
      <div>
        <h3 className="text-2xl font-semibold">Datasets</h3>
      </div>
      <div className="flex justify-between me-9 mt-6 mb-3">
        <select className="select-type w-[90px] px-2 py-2 border-2 border-gray-300 rounded-2xl text-gray-900 text-sm focus:ring-gray-500 focus:border-gray-500 ">
          <option className="">Owner</option>
          <option className="">Owner</option>
          <option className="">Owner</option>
        </select>
        <AddButton value={"New Dataset"} link={"/add-dataset"} />
      </div>

      <div className=" pe-9 ">
        <CommonTable data={datasets} tableHead={"Experiment"} />
      </div>
    </div>
  );
};

export default Datasets;
