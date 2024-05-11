import axios from "axios";
import React, { useState } from "react";
import { FaArrowLeft, FaPlus } from "react-icons/fa6";
import { useQuery } from "react-query";
import { Link, useNavigate } from "react-router-dom";
import Loading from "../../../components/Loading";
import { IoIosArrowForward } from "react-icons/io";
import CommonButton from "../../../components/AddButton";
import { ImPencil } from "react-icons/im";
import { IoMdClose } from "react-icons/io";
import AddDatabaseModal from "../../../components/AddDatabaseModal";

const AddExperiment2 = () => {
  const [selectedOption, setSelectedOption] = useState("");
  const [selectedDatabase, setSelectedDatabase] = useState("");
  const [openModal, setOpenModal] = useState(false);
  const navigate = useNavigate();

  // const { data: experiments, isLoading } = useQuery({
  //   queryKey: ["experiments"],
  //   queryFn: async () => {
  //     const res = await axios.get("${process.env.REACT_APP_BASE_URL}/experiment");
  //     const data = await res.data;
  //     return data;
  //   },
  // });

  const handleSelectChange = (event) => {
    setSelectedOption(event.target.value);
  };
  const handleChangeSelectedDatabase = (event) => {
    setSelectedDatabase(event.target.value);
  };

  // if (isLoading) return <Loading />;
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
      <div className="w-full bg-[#fbeaee] flex items-center">
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
                <p>{selectedDatabase}</p>
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
            <option value="Ahmed">Ahmed</option>
            <option value="Other">Other</option>
            {/* Add other options here */}
          </select>
          <div>
            <button onClick={() => setOpenModal(true)} className="px-2 flex items-center gap-2 text-[#E5227A] text-[12px] rounded  font-semibold">
              <FaPlus />
              Add new database
            </button>
          </div>
        </div>
      </div>
      <AddDatabaseModal open={openModal} onClose={() => setOpenModal(false)}/>
      <div className="ps-7 mt-7">
        <form>
          <div className="flex flex-col mb-7">
            <label className="text-[12px] font-bold mb-[2px]">
              Number of read/write containers
            </label>
            <input
              className="search-input w-[250px] px-2 py-1 border border-[#E0E0E0] rounded"
              type="number"
              name=""
              id=""
              placeholder="Enter Number"
            />
          </div>
          <div className="flex flex-col mb-7">
            <label className="text-[12px] font-bold mb-[2px]">Query</label>
            <textarea
              className="search-input max-w-[400px] h-[64px] px-2 py-1 border border-[#E0E0E0] rounded resize-none"
              type="number"
              name=""
              id=""
              placeholder="Enter Number"
            />
          </div>
          <div>
            <label className="text-[12px] font-bold mb-[2px]">
              Concurrency Level
            </label>
            <select
              className="block w-[250px] px-2 py-2 border-2 border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-gray-500"
              style={{
                fontSize: "14px",
                color: "#8E8E8E",
              }}
              value={selectedOption}
              onChange={handleSelectChange}
            >
              <option value="" disabled>
                Choose level
              </option>
              <option value="Ahmed">Ahmed</option>
              <option value="Other">Other</option>
              {/* Add other options here */}
            </select>
          </div>
          <div className="mt-[200px] flex gap-2">
            <CommonButton value={"Launch"} />
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
