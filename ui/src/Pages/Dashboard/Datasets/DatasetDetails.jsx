/* eslint-disable no-unused-vars */
// DatasetDetails.js
import React, { useEffect, useState } from "react";
import { FaArrowLeft } from "react-icons/fa6";
import { useNavigate, useParams } from "react-router-dom";
import DatasetDetailsTable from "../../../components/DatasetDetails/DatasetDetailsTable";
import { motion, AnimatePresence } from 'framer-motion';
import axios from "axios";
import Loading from "../../../components/Common/Loading";
import DatasetDetailsTabs from "../../../components/DatasetDetails/DatasetDetailsTabs";
import fetchDatasetData from "../../../api/fetchDatasetData";



const DatasetDetails = () => {
  const navigate = useNavigate();
  const [datasetData, setDatasetData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState(0);
  const [editableTableName, setEditableTableName] = useState(null);

  const { id } = useParams();

  if(!id){
    navigate("/dataset");
  }
  const handleDoubleClick = (index) => {
    setEditableTableName(index);
  };

  const handleNameChange = (event, index) => {
    if (event.key === "Enter") {
      const newDatasetData = [...datasetData];
      newDatasetData[index].tableName = event.target.value;
      setEditableTableName(null);
      // here we will call the put request for update the table naem
    }
  };

  useEffect(() => {
    fetchDatasetData(id, setDatasetData, setLoading)
  }, [id])


  const tabsProps = {
    datasetData,
    activeTab,
    setActiveTab,
    handleDoubleClick,
    editableTableName,
    handleNameChange,
    setEditableTableName,
  }

  if (loading) return <Loading />;

  return (
    <div className="py-8">
      <div className="ps-7 mb-5 flex items-center gap-3 -tracking-tighter">
        <FaArrowLeft
          className="cursor-pointer"
          onClick={() => navigate("/")}
          size={20}
        />
        <h2 className="text-[#8e8e8e] text-xl font-semibold">Dataset /</h2>
        <h2 className="text-xl font-semibold">{id}</h2>
      </div>
      <div className="w-[95%] h-[1px] bg-accent my-6"></div>

      <div className="ps-7 pe-9">
      {datasetData && (
        <div>
          <DatasetDetailsTabs props={tabsProps}/>
          <AnimatePresence mode="wait">
            <motion.div
              key={activeTab}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              transition={{ duration: 0.3 }}
            >
              <DatasetDetailsTable data={datasetData[activeTab]} />
            </motion.div>
          </AnimatePresence>
        </div>
      )}
    </div>
    </div>
  );
};

export default DatasetDetails;
