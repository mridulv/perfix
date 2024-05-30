import React, { useState } from "react";
import ChooseDatasetComponent from "../../../components/ChooseDatasetComponent";
import axios from "axios";
import { useQuery } from "react-query";
import { handleAddDatasetApi } from "../../../utilities/api";
import Loading from "../../../components/Loading";
import { useNavigate } from "react-router-dom";

const AddDBConfigurationDataset = () => {
  const [activeDataset, setActiveDataset] = useState("new");
  const [columns, setColumns] = useState([{ columnName: "", columnType: "" }]);

  const navigate = useNavigate();

  const { data: datasets, isLoading: datasetsLoading } = useQuery({
    queryKey: ["datasets"],
    queryFn: async () => {
      const values = [];
      const res = await axios.post(
        `${process.env.REACT_APP_BASE_URL}/dataset`,
        values,
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      const data = await res.data;
      return data;
    },
  });

  const handleAddColumn = () => {
    setColumns([...columns, { columnName: "", columnType: "" }]);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    const datasetId = event.target.datasetId.value;

    if (activeDataset === "new") {
      const navigateFor = "databasePage";
      handleAddDatasetApi(
        event,
        datasets,
        columns,
        setColumns,
        navigate,
        navigateFor
      );
    } else {
      navigate(`/add-database/${datasetId}`);
    }
  };

  if(datasetsLoading) return <Loading/>
  return (
    <div className="ps-7 pt-7">
      <h3 className="text-xl font-semibold">Create Dataset</h3>
      <div>
        <ChooseDatasetComponent
          activeDataset={activeDataset}
          setActiveDataset={setActiveDataset}
          handleAddColumn={handleAddColumn}
          handleSubmit={handleSubmit}
          columns={columns}
          setColumns={setColumns}
          datasets={datasets}
        />
      </div>
    </div>
  );
};

export default AddDBConfigurationDataset;
