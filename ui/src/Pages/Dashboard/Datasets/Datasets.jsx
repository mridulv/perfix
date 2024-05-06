import React from "react";
import axios from "axios";
import { FaPlus } from "react-icons/fa6";
import { useQuery } from "react-query";
import toast from "react-hot-toast";
import DatasetCard from "../../../components/DatasetCard";
import Loading from "../../../components/Loading";
import AddButton from "../../../components/AddButton";

const Datasets = () => {
  const {
    data: datasets,
    isLoading,
    refetch,
  } = useQuery({
    queryKey: ["datasets"],
    queryFn: async () => {
      const res = await axios.get("http://localhost:9000/dataset");
      const data = await res.data;
      console.log(data);
      return data;
    },
  });

  if (isLoading) return <Loading />;
  return (
    <div className="px-3 py-4">
      <h2 className="text-2xl text-center font-bold text-orange-600 my-3">
        Datasets
      </h2>
      <div className="flex justify-end my-4 mx-8">
        <AddButton value={"New Dataset"} link={"/add-dataset"} />
      </div>

      <div className="w-[90%] mx-auto grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {datasets &&
          datasets.map((dataset) => (
            <DatasetCard key={dataset.id.id} dataset={dataset}></DatasetCard>
          ))}
      </div>
    </div>
  );
};

export default Datasets;
