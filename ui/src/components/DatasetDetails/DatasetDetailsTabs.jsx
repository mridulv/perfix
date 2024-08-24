/* eslint-disable no-unused-vars */
import React from "react";
import {motion} from "framer-motion";

const DatasetDetailsTabs = ({ props }) => {
  const {
    datasetData,
    activeTab,
    setActiveTab,
    handleDoubleClick,
    editableTableName,
    handleNameChange,
    setEditableTableName,
  } = props;
  return (
    <div className="flex justify-center mb-4">
      <div className="w-full border-b border-gray-200 relative">
        <div className="flex">
          {datasetData.map((dataset, index) => (
            <div
              key={index}
              className={`w-[110px] me-10  px-2 py-2 cursor-pointer transition-all duration-300 ease-in-out relative ${
                activeTab === index
                  ? "text-primary"
                  : "text-gray-500 hover:text-gray-700"
              }`}
              onClick={() => setActiveTab(index)}
              onDoubleClick={() => handleDoubleClick(index)}
            >
              {editableTableName === index ? (
                <input
                  type="text"
                  defaultValue={dataset.tableName}
                  onKeyDown={(e) => handleNameChange(e, index)}
                  autoFocus
                  onBlur={() => setEditableTableName(null)}
                  className="bg-transparent outline-none w-full"
                  style={{
                    maxWidth: "100%",
                    minWidth: "0",
                  }}
                />
              ) : (
                <span className="w-full truncate">{dataset.tableName}</span>
              )}
              {activeTab === index && (
                <motion.div
                  className="absolute bottom-0 left-0 w-full h-[2px] bg-primary"
                  style={{ bottom: "-1px" }}
                  layoutId="activeTab"
                  transition={{ type: "spring", stiffness: 500, damping: 30 }}
                />
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default DatasetDetailsTabs;
