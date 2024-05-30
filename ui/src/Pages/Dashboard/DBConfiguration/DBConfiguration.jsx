import React, { useEffect, useState } from "react";
import CommonTable from "../../../components/CommonTable";
import { useQuery } from "react-query";
import axios from "axios";
import Loading from "../../../components/Loading";
import CustomSelect from "../../../components/CustomSelect";
import AddDatabaseModal from "../../../components/AddDatabaseModal";

const columnHeads = [
  "Database Name",
  "Database Type",
  "Dataset Name",
  "Created At",
];
const databaseTypeOptions = [
  { option: "MySQL", value: "MySQL" },
  { option: "DynamoDB", value: "DynamoDB" },
  { option: "Redis", value: "Redis" },
  { option: "DocumentDB", value: "DocumentDB" },
];

const DBConfiguration = () => {
  const [selectedDatabaseType, setSelectedDatabaseType] = useState({
    option: "Choose Type",
    value: "choose",
  });
  const [open, setOpen] = useState(false);
  const [datasets, setDatasets] = useState([]);
  const [datasetsLoading, setDatasetsLoading] = useState(false);

  const { data: databases, isLoading } = useQuery({
    queryKey: ["databases", selectedDatabaseType],
    queryFn: async () => {
      let values = [];

      if (selectedDatabaseType.value !== "choose") {
        values = [
          { store: selectedDatabaseType.value, type: "DatabaseTypeFilter" },
        ];
      }

      const res = await axios.post(
        `${process.env.REACT_APP_BASE_URL}/config`,
        values,
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );

      const data = await res.data;
      return data;
    },
  });

  useEffect(() => {
    const value = [];
    setDatasetsLoading(true);
    const fetchDatasets = async () => {
      const res = await axios.post(
        `${process.env.REACT_APP_BASE_URL}/dataset`,
        value,
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );
      if (res.status === 200) {
        setDatasets(res.data);
        setDatasetsLoading(false);
      }
    };
    fetchDatasets();
  }, []);

  const dataForTable = databases?.map((database) => ({
    databaseName: database.name,
    databaseType: database.storeParams.type,
    datasetName: "Pending",
    createdAt: new Date(database.createdAt).toLocaleDateString(),
  }));

  if (isLoading && datasetsLoading) return <Loading />;
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
          />
          <div className="ms-3">
            <label className="text-15px">Filter: </label>

            <CustomSelect
              selected={selectedDatabaseType}
              setSelected={setSelectedDatabaseType}
              options={databaseTypeOptions}
            />
          </div>
        </div>
        <div>
          <button
            onClick={() => setOpen(true)}
            className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#57B1FF]"
          >
            Add Database
          </button>
        </div>
        <AddDatabaseModal
          open={open}
          onClose={() => setOpen(false)}
          datasets={datasets}
        />
      </div>
      <div className="me-9">
        <div className="">
          <CommonTable
            data={dataForTable}
            tableHead={"Database"}
            columnHeads={columnHeads}
          />
        </div>
      </div>
    </div>
  );
};

export default DBConfiguration;
