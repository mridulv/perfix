import axios from "axios";
import React, { useState } from "react";
import toast from "react-hot-toast";

const AddDatabase = ({
  dataset,
  onClose = null,
  navigate = null,
  valueFor,
  setSelectedDatabase = null,
  refetch = null,
}) => {
  const [selectedOption, setSelectedOption] = useState("");
  const [currentStep, setCurrentStep] = useState(1);
  const [formData, setFormData] = useState({
    name: "",
    database: "",
    nodeType: "",
    cacheNodes: 0,
    instanceClass: "",
    collectionName: "",
    instanceType: "",
    tableName: "",
    rcu: 0,
    wcu: 0,
    primaryIndex: "",
    secondaryIndex: "",
    keyColumn: "",
    gsis: "",
    columnsAsIndexes: "",
  });

  const handleSelectChange = (event) => {
    setSelectedOption(event.target.value);
    setFormData({ ...formData, database: event.target.value });
  };

  const isCurrentStepValid = () => {
    if (currentStep === 1) {
      return formData.name && formData.database;
    }

    if (currentStep === 2) {
      if (selectedOption === "Redis") {
        return formData.nodeType && formData.cacheNodes;
      } else if (selectedOption === "DocumentDB") {
        return formData.instanceClass && formData.collectionName;
      } else if (selectedOption === "MySQL") {
        return formData.instanceType && formData.tableName;
      } else if (selectedOption === "DynamoDB") {
        return formData.rcu && formData.wcu;
      } else {
        return formData;
      }
    }
    if (currentStep === 3) {
      if (selectedOption === "Redis") {
        return formData.keyColumn;
      } else if (selectedOption === "DocumentDB") {
        return formData.columnsAsIndexes;
      } else if (selectedOption === "MySQL") {
        return formData.primaryIndex && formData.secondaryIndex;
      } else if (selectedOption === "DynamoDB") {
        return formData.gsis;
      } else {
        return formData;
      }
    }

    return true;
  };

  const handleCurrentStep = () => {
    if (!isCurrentStepValid()) {
      toast.error("Please fill in all required fields for this step.");
      return;
    }

    setCurrentStep(currentStep + 1);
  };

  const handleCancel = () => {
    if (valueFor === "modal") {
      onClose();
    } else if (valueFor === "page") {
      navigate("/database");
    }
    setCurrentStep(1);
    setFormData({
      name: "",
      database: "",
      nodeType: "",
      cacheNodes: 0,
      instanceClass: "",
      collectionName: "",
      instanceType: "",
      tableName: "",
      rcu: 0,
      wcu: 0,
      primaryIndex: "",
      secondaryIndex: "",
      keyColumn: "",
      gsis: "",
      columnsAsIndexes: "",
    });
  };

  let storeParams = {};

  if (selectedOption === "Redis") {
    storeParams = {
      ...storeParams,
      nodeType: formData.nodeType,
      cacheNodes: Number(formData.cacheNodes),
      keyColumn: formData.keyColumn,
      type: "Redis",
    };
  } else if (selectedOption === "DocumentDB") {
    storeParams = {
      ...storeParams,
      instanceClass: formData.instanceClass,
      collectionName: formData.collectionName,
      columnsAsIndexes: formData.columnsAsIndexes,
      type: "DocumentDB",
    };
  } else if (selectedOption === "MySQL") {
    storeParams = {
      ...storeParams,
      instanceType: formData.instanceType,
      tableName: formData.tableName,
      primaryIndex: formData.primaryIndex,
      secondaryIndex: formData.secondaryIndex,
      type: "MySQL",
    };
  } else if (selectedOption === "DynamoDB") {
    storeParams = {
      ...storeParams,
      rcu: Number(formData.rcu),
      wcu: Number(formData.wcu),
      type: "DynamoDB",
    };
  } else {
    storeParams = {};
  }
  const handleAddDatabase = async (e) => {
    e.preventDefault();

    let values = {
      name: formData.name,
      dataStore:
        selectedOption === "MySQL"
          ? "MySQLStoreType"
          : selectedOption === "Redis"
          ? "RedisStoreType"
          : selectedOption === "DocumentDB"
          ? "MongoDBStoreType"
          : "DynamoDBStoreType",
      datasetId: { id: dataset.id.id },
      storeParams,
    };
    console.log(values);
    if (isCurrentStepValid()) {
      const res = await axios.post(`${process.env.REACT_APP_BASE_URL}/config/create`, values, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      console.log(res);
      if (res.status === 200) {
        if (valueFor === "modal") {
          toast.success("Database added successfully.");
          refetch();
          setSelectedDatabase(res.data.id);
          onClose();
        } else {
          toast.success("Database added successfully.");
          navigate("/database");
        }
      }
    }
  };
  return (
    <div className=" ps-7 flex flex-col relative">
      <form className="" onSubmit={handleAddDatabase}>
        <div>
          {currentStep === 1 && (
            <div>
              <div className="flex flex-col mb-4">
                <label className="text-[12px] font-bold mb-[2px]">Name</label>
                <input
                  className="search-input border-2 border-gray-300 focus:border-gray-400 outline-pink-600 max-w-[250px] px-2 py-2 rounded"
                  placeholder="Name"
                  type="text"
                  required
                  value={formData.name}
                  onChange={(e) =>
                    setFormData({ ...formData, name: e.target.value })
                  }
                />
              </div>
              <div>
                <label className="text-[12px] font-bold mb-[2px]">
                  Database type
                </label>
                <select
                  className="block w-[250px] px-2 py-2 border-2 border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-gray-500"
                  style={{
                    fontSize: "14px",
                    color: "#8E8E8E",
                  }}
                  value={formData.database}
                  onChange={handleSelectChange}
                  required
                >
                  <option value="" disabled>
                    Choose type
                  </option>
                  <option value="Redis">Redis</option>
                  <option value="DocumentDB">DocumentDB</option>
                  <option value="MySQL">MySQL</option>
                  <option value="DynamoDB">DynamoDB</option>
                </select>
              </div>
            </div>
          )}
          {currentStep === 2 && (
            <>
              {selectedOption === "Redis" && (
                <div className="flex flex-col gap-4">
                  <div>
                    <label className="text-[12px] font-bold mb-[2px]">
                      NodeType
                    </label>
                    <select
                      className="block w-[250px] px-2 py-2 border-2 border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-gray-500"
                      style={{
                        fontSize: "14px",
                        color: "#8E8E8E",
                      }}
                      value={formData.nodeType}
                      onChange={(e) =>
                        setFormData({ ...formData, nodeType: e.target.value })
                      }
                      required
                    >
                      <option value="" disabled>
                        Choose type
                      </option>
                      <option value="first">First</option>
                      <option value="second">Second</option>
                    </select>
                  </div>
                  <div className="flex flex-col">
                    <label className="text-[12px] font-bold mb-[2px]">
                      Number of Cache Nodes
                    </label>
                    <input
                      className="search-input border-2 border-gray-300 focus:border-gray-400 outline-pink-600 max-w-[250px] px-2 py-2 rounded"
                      placeholder="number"
                      type="number"
                      required
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          cacheNodes: e.target.value,
                        })
                      }
                    />
                  </div>
                </div>
              )}
              {selectedOption === "DocumentDB" && (
                <div className="flex flex-col gap-4">
                  <div>
                    <label className="text-[12px] font-bold mb-[2px]">
                      InstanceClass
                    </label>
                    <select
                      className="block w-[250px] px-2 py-2 border-2 border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-gray-500"
                      style={{
                        fontSize: "14px",
                        color: "#8E8E8E",
                      }}
                      value={formData.instanceClass}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          instanceClass: e.target.value,
                        })
                      }
                      required
                    >
                      <option value="" disabled>
                        Choose type
                      </option>
                      <option value="first">First</option>
                      <option value="second">Second</option>
                    </select>
                  </div>
                  <div className="flex flex-col">
                    <label className="text-[12px] font-bold mb-[2px]">
                      Collection Name
                    </label>
                    <input
                      className="search-input border-2 border-gray-300 focus:border-gray-400 outline-pink-600 max-w-[250px] px-2 py-2 rounded"
                      placeholder="name"
                      type="text"
                      required
                      value={formData.collectionName}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          collectionName: e.target.value,
                        })
                      }
                    />
                  </div>
                </div>
              )}
              {selectedOption === "MySQL" && (
                <div className="flex flex-col gap-4">
                  <div>
                    <label className="text-[12px] font-bold mb-[2px]">
                      InstanceType
                    </label>
                    <select
                      className="block w-[250px] px-2 py-2 border-2 border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-gray-500"
                      style={{
                        fontSize: "14px",
                        color: "#8E8E8E",
                      }}
                      value={formData.instanceType}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          instanceType: e.target.value,
                        })
                      }
                      required
                    >
                      <option value="" disabled>
                        Choose type
                      </option>
                      <option value="first">First</option>
                      <option value="second">Second</option>
                    </select>
                  </div>
                  <div className="flex flex-col">
                    <label className="text-[12px] font-bold mb-[2px]">
                      Table Name
                    </label>
                    <input
                      className="search-input border-2 border-gray-300 focus:border-gray-400 outline-pink-600 max-w-[250px] px-2 py-2 rounded"
                      placeholder="name"
                      type="text"
                      required
                      value={formData.tableName}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          tableName: e.target.value,
                        })
                      }
                    />
                  </div>
                </div>
              )}
              {selectedOption === "DynamoDB" && (
                <div className="flex flex-col gap-4">
                  <div className="flex flex-col">
                    <label className="text-[12px] font-bold mb-[2px]">
                      RCU
                    </label>
                    <input
                      className="search-input border-2 border-gray-300 focus:border-gray-400 outline-pink-600 max-w-[250px] px-2 py-2 rounded"
                      placeholder="name"
                      type="number"
                      required
                      value={formData.rcu}
                      onChange={(e) =>
                        setFormData({ ...formData, rcu: e.target.value })
                      }
                      min={1}
                      max={100}
                    />
                  </div>
                  <div className="flex flex-col">
                    <label className="text-[12px] font-bold mb-[2px]">
                      WCU
                    </label>
                    <input
                      className="search-input border-2 border-gray-300 focus:border-gray-400 outline-pink-600 max-w-[250px] px-2 py-2 rounded"
                      placeholder="name"
                      type="number"
                      required
                      value={formData.wcu}
                      onChange={(e) =>
                        setFormData({ ...formData, wcu: e.target.value })
                      }
                      min={1}
                      max={2}
                    />
                  </div>
                </div>
              )}
            </>
          )}
          {currentStep === 3 && (
            <>
              {selectedOption === "MySQL" && (
                <div>
                  <div>
                    <label className="text-[12px] font-bold mb-[2px]">
                      PrimaryIndex
                    </label>
                    <select
                      className="block w-[250px] px-2 py-2 border-2 border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-gray-500"
                      style={{
                        fontSize: "14px",
                        color: "#8E8E8E",
                      }}
                      value={formData.primaryIndex}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          primaryIndex: e.target.value,
                        })
                      }
                    >
                      <option value="" disabled>
                        Choose column
                      </option>
                      {dataset.columns.map((col, i) => (
                        <option key={i}>{col.columnName}</option>
                      ))}
                    </select>
                  </div>
                  <div>
                    <label className="text-[12px] font-bold mb-[2px]">
                      SecondaryIndex
                    </label>
                    <select
                      className="block w-[250px] px-2 py-2 border-2 border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-gray-500"
                      style={{
                        fontSize: "14px",
                        color: "#8E8E8E",
                      }}
                      value={formData.secondaryIndex}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          secondaryIndex: e.target.value,
                        })
                      }
                    >
                      <option value="" disabled>
                        Choose columns
                      </option>
                      {dataset.columns.map((col, i) => (
                        <option key={i}>{col.columnName}</option>
                      ))}
                    </select>
                  </div>
                </div>
              )}
              {selectedOption === "Redis" && (
                <div>
                  <label className="text-[12px] font-bold mb-[2px]">
                    Select a Key Column
                  </label>
                  <select
                    className="block w-[250px] px-2 py-2 border-2 border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-gray-500"
                    style={{
                      fontSize: "14px",
                      color: "#8E8E8E",
                    }}
                    value={formData.keyColumn}
                    onChange={(e) =>
                      setFormData({ ...formData, keyColumn: e.target.value })
                    }
                  >
                    <option value="" disabled>
                      Choose column
                    </option>
                    {dataset.columns.map((col, i) => (
                      <option key={i}>{col.columnName}</option>
                    ))}
                  </select>
                </div>
              )}
              {selectedOption === "DocumentDB" && (
                <div>
                  <label className="text-[12px] font-bold mb-[2px]">
                    Select Columns as Indexes
                  </label>
                  <select
                    className="block w-[250px] px-2 py-2 border-2 border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-gray-500"
                    style={{
                      fontSize: "14px",
                      color: "#8E8E8E",
                    }}
                    value={formData.columnsAsIndexes}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        columnsAsIndexes: e.target.value,
                      })
                    }
                  >
                    <option value="" disabled>
                      Choose columns
                    </option>
                    {dataset.columns.map((col, i) => (
                      <option key={i}>{col.columnName}</option>
                    ))}
                  </select>
                </div>
              )}
            </>
          )}
        </div>
        <div className="mt-[180px]">
          <div className="mb-5 flex justify-center gap-3">
            <div
              className={`bg-primary w-[10px] h-[10px] rounded-full ${
                currentStep === 1 ? "opacity-100" : "opacity-30"
              }`}
            ></div>
            <div
              className={`bg-primary w-[10px] h-[10px] rounded-full ${
                currentStep === 2 ? "opacity-100" : "opacity-30"
              }`}
            ></div>
            <div
              className={`bg-primary w-[10px] h-[10px] rounded-full ${
                currentStep === 3 ? "opacity-100" : "opacity-30"
              }`}
            ></div>
          </div>
          <div className=" pe-4 flex justify-end gap-2">
            <button
              onClick={handleCancel}
              type="button"
              className="px-3 py-1 text-[14px] font-semibold border-2 border-gray-300 rounded "
            >
              Cancel
            </button>
            {currentStep < 3 ? (
              <button
                type="button"
                onClick={handleCurrentStep}
                className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#6b3b51d2]"
              >
                Next
              </button>
            ) : (
              <button
                type="submit"
                className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#6b3b51d2]"
              >
                Save
              </button>
            )}
          </div>
        </div>
      </form>
    </div>
  );
};

export default AddDatabase;
