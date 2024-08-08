/* eslint-disable no-unused-vars */
import React from "react";
import { useNavigate } from "react-router-dom";
import axiosApi from "../../../api/axios";
import Loading from "../../../components/Common/Loading";
import toast from "react-hot-toast";
import { useEffect, useState } from "react";
import useUseCases from "../../../api/useUseCases";
import CommonTable from "../../../components/CommonTable/CommonTable";

const columnHeads = ["Use Cases", "Created At", "Status"];
const UseCases = () => {
  const [searchText, setSearchText] = useState("");
  const [filterValues, setFilterValues] = useState([]);
  const navigate = useNavigate();

  const handleSearchText = (e) => {
    setSearchText(e.target.value);
  };

  useEffect(() => {
    const newFilterValues = [];

    if (searchText) {
      newFilterValues.push({ text: searchText, type: "TextFilter" });
    }

    setFilterValues(newFilterValues);
  }, [searchText]);

  const { data: useCases, isLoading, refetch } = useUseCases(filterValues);

  const handleClearFilters = () => {
    setSearchText("");
  }

  const handleNewConversation = async () => {
    const res = await axiosApi.post(`/usecases/create`, {});
    if (res.status === 200) {
      const id = res.data.useCaseId.id;
      navigate(`/usecases/${id}`);
    } else {
      toast.success("Couldn't create a conversation");
    }
  };

  return (
    <div>
      <div className="pt-7 ps-7">
        <h3 className="text-2xl font-semibold -tracking-tighter">Use Cases</h3>
      </div>
      <div className="w-[95%] h-[1px] bg-accent my-6"></div>
      <div className="mb-3 ps-7 pe-9 flex justify-between">
        <div className="flex gap-x-4">
          <input
            className=" search-input"
            type="text"
            placeholder="Search"
            onChange={handleSearchText}
            value={searchText}
          />
          {
            searchText && (
              <button
              onClick={handleClearFilters}
              className="text-sm font-semibold tracking-wider"
            >
              Clear
            </button>
            )
          }
        </div>
        <button
          onClick={handleNewConversation}
          className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#57B1FF]"
        >
          New Use Case
        </button>
      </div>

      <div className="mt-6 ps-7 pe-9">
        <h3 className="text-xl font-semibold mb-4">Previous Use Cases</h3>
        <div>
          {isLoading ? (
            <Loading />
          ) : (
            <CommonTable
              data={useCases}
              tableHead={"Use Cases"}
              columnHeads={columnHeads}
              refetch={refetch}
              data-testid="usecases-table"
            />
          )}
        </div>
      </div>
    </div>
  );
};

export default UseCases;
