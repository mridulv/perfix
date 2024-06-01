import React, { useState } from "react";
import { FaArrowLeft, FaPlus } from "react-icons/fa6";
import { Link, useNavigate, useParams } from "react-router-dom";
import { IoIosArrowForward } from "react-icons/io";
import { ImPencil } from "react-icons/im";
import { IoMdClose } from "react-icons/io";
import AddDatabaseModalForExperiment from "../../../components/AddDatabaseModalForExperiment";
import { useQuery } from "react-query";
import axios from "axios";
import Loading from "../../../components/Loading";
import toast from "react-hot-toast";
import RangeInput from "../../../components/RangeInput";

const AddExperiment2 = () => {
  const { datasetId } = useParams();

  // const [selectedOption, setSelectedOption] = useState("");
  const [selectedDatabase, setSelectedDatabase] = useState("");
  const [openModal, setOpenModal] = useState(false);
  const [writeBatchSizeValue, setWriteBatchSizeValue] = useState(0);
  const [concurrentQueries, setConcurrentQueries] = useState(0);
  const [experimentTimeInSecond, setExperimentTimeInSecond] = useState(0);

  const navigate = useNavigate();

  const { data: dataset, isLoading: isDatasetLoading } = useQuery({
    queryKey: ["dataset", datasetId],
    queryFn: async () => {
      const res = await axios.get(`${process.env.REACT_APP_BASE_URL}/dataset/${datasetId}`, {
        withCredentials: true,
      });
      const data = await res.data;
      return data;
    },
  });

  const {
    data: databases,
    isLoading: isDatabasesLoading,
    refetch,
  } = useQuery({
    queryKey: ["databases"],
    queryFn: async () => {
      const values = [];
      const res = await axios.post(`${process.env.REACT_APP_BASE_URL}/config`, values, {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true,
      });
      const data = await res.data;
      return data;
    },
  });

  const databasesObj = databases
    ? Object.fromEntries(
        databases.map((database) => [
          database.databaseConfigId.id,
          database.name,
        ])
      )
    : {};

  // const handleSelectChange = (event) => {
  //   setSelectedOption(event.target.value);
  // };
  const handleChangeSelectedDatabase = (event) => {
    setSelectedDatabase(event.target.value);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const form = event.target;
    const name = form.name.value;

    const limitOpt = Number(form.limitOpt.value);

    const values = {
      name,
      writeBatchSize: Number(writeBatchSizeValue),
      concurrentQueries: Number(concurrentQueries),
      experimentTimeInSeconds: Number(experimentTimeInSecond),
      query: {
        limitOpt,
      },
      databaseConfigId: {
        id: Number(selectedDatabase),
      },
    };

    if (!selectedDatabase) return toast.error("Please select a database.");

    const res = await axios.post(`${process.env.REACT_APP_BASE_URL}/experiment/create`, values, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    if (res.status === 200) {
      toast.success("Experiment added successfully");
      navigate("/experiment");
    }
  };

  if (isDatasetLoading && isDatabasesLoading) return <Loading />;
  return (
    <div className="pt-8 ">
      <div className="ps-7 mb-5 flex items-center gap-3">
        <FaArrowLeft
          className="cursor-pointer"
          onClick={() => navigate("/experiment")}
          size={20}
        />
        <h2 className="text-[#8e8e8e] text-xl font-semibold">
          Create new Experiment /
        </h2>
        <h2 className="text-xl font-semibold">
          Experiment {/*{experiments?.length + 1}*/}
        </h2>
      </div>
      <div className="w-full bg-secondary flex items-center">
        <div className="ps-7 py-3 ">
          <div
            className={`h-[45px] w-[180px] ps-3 flex items-center gap-3 rounded-xl`}
          >
            <div className="h-6 w-6 rounded-full bg-black text-white flex justify-center items-center">
              <p className="text-[14px]">1</p>
            </div>
            <p className="font-bold text-[14px]">Setup Datasets</p>
          </div>
        </div>
        <IoIosArrowForward className="mx-7" size={20} />
        <div
          className={`h-[45px] w-[180px] ps-3 bg-white flex items-center gap-3 rounded-xl`}
        >
          <div className="h-6 w-6 rounded-full bg-black text-white flex justify-center items-center">
            <p className="text-[14px]">2</p>
          </div>
          <p className="font-bold text-[14px]">Setup Database</p>
        </div>
      </div>

      <div className="ps-7 mt-6">
        <p className="text-[12px] font-bold mb-[2px] block">Choose Database</p>
        <div className="flex items-center gap-3">
          <div
            className={`min-w-[140px] h-[35px] flex items-center justify-center rounded-2xl text-[12px] font-semibold ${
              !selectedDatabase ? "bg-gray-300" : "bg-none"
            } `}
          >
            {selectedDatabase ? (
              <div
                className={`w-full h-full px-1 flex gap-2 items-center justify-center bg-[#FDD3DB] rounded-2xl`}
              >
                <p>{databasesObj[selectedDatabase]}</p>
                <ImPencil size={12} />
                <IoMdClose
                  title="Remove Database"
                  onClick={() => setSelectedDatabase("")}
                  cursor={"pointer"}
                  size={18}
                />
              </div>
            ) : (
              <>
                <p>Nothing selected</p>
              </>
            )}
          </div>
          <select
            className="block w-[175px] me-3 px-2 py-2 border-2 border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-gray-500"
            style={{
              fontSize: "14px",
              color: "#8E8E8E",
            }}
            value={selectedDatabase}
            onChange={handleChangeSelectedDatabase}
          >
            <option value="" disabled>
              Choose Databases
            </option>
            {databases?.map((database) => (
              <option
                key={database.databaseConfigId.id}
                value={database.databaseConfigId.id}
              >
                {database.name}
              </option>
            ))}
          </select>
          <div>
            <button
              onClick={() => setOpenModal(true)}
              className="px-2 flex items-center gap-2 text-primary text-[12px] rounded  font-semibold"
            >
              <FaPlus />
              Add new database
            </button>
          </div>
        </div>
      </div>
      <AddDatabaseModalForExperiment
        open={openModal}
        onClose={() => setOpenModal(false)}
        dataset={dataset}
        setSelectedDatabase={setSelectedDatabase}
        refetch={refetch}
      />
      <div className="ps-7 mt-7">
        <form onSubmit={handleSubmit}>
          <div className="flex flex-col mb-7">
            <label className="text-[12px] font-bold mb-[2px]">Name</label>
            <input
              className="search-input w-[250px] px-2 py-1 border border-[#E0E0E0] rounded"
              type="text"
              name="name"
              placeholder="Enter Number"
              required
            />
          </div>
          <div className="mb-4">
            <RangeInput
              value={writeBatchSizeValue}
              setValue={setWriteBatchSizeValue}
              startValue={"0mhz"}
              lastValue={"1500mhz"}
              label={"Write Batch Size"}
            />
          </div>
          <div className="mb-4">
            <RangeInput
              value={concurrentQueries}
              setValue={setConcurrentQueries}
              startValue={"0mhz"}
              lastValue={"1500mhz"}
              label={"Concurrent Queries"}
            />
          </div>
          <div className="mb-4">
            <RangeInput
              value={experimentTimeInSecond}
              setValue={setExperimentTimeInSecond}
              startValue={"0mhz"}
              lastValue={"1500mhz"}
              label={"Experiment Time In Seconds"}
            />
          </div>
          <div className="flex flex-col mb-7">
            <label className="text-[12px] font-bold mb-[2px]">Query</label>
            <input
              className="search-input w-[250px] px-2 py-1 border border-[#E0E0E0] rounded"
              type="number"
              name="limitOpt"
              placeholder="Enter Number"
              required
            />
          </div>

          <div className="mt-[200px] flex gap-2">
            <button
              className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#6b3b51d2]"
              type="submit"
            >
              Launch
            </button>
            <Link
              to={"/experiment"}
              className="px-3 py-1 text-[14px] font-bold border-2 border-gray-300 rounded"
            >
              Cancel
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddExperiment2;
