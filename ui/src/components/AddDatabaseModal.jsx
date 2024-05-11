import React, { useState } from "react";
import toast from "react-hot-toast";
import { MdClose } from "react-icons/md";

const AddDatabaseModal = ({ open, onClose }) => {
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
  })

  const handleSelectChange = (event) => {
    setSelectedOption(event.target.value);
    setFormData({...formData, database: event.target.value})
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
      } else if(selectedOption === "MySQL"){
        return formData.instanceType && formData.tableName;
      } else if(selectedOption === "DynamoDB"){
        return formData.rcu && formData.wcu;
      } else{
        return formData;
      }
    }
  
    return true; 
  };

  const handleCurrentStep = () => {
  if (!isCurrentStepValid()) {
    toast.error("Please fill in all required fields for this step.")
    return;
  }

  setCurrentStep(currentStep + 1);
};

  const handleCancel = () => {
    onClose();
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
    })
  }

  const handleAddDatabase = (e) => {
    e.preventDefault();
    let values = {
      name: formData.name,
      databaseType: formData.database
    };

    if (selectedOption === "Redis") {
      values = {...values, nodeType: formData.nodeType, cacheNodes : Number(formData.cacheNodes)}
    } else if (selectedOption === "DocumentDB") {
      values =  {...values, instanceClass: formData.instanceClass, collectionName: formData.collectionName}
    } else if(selectedOption === "MySQL"){
      values = {...values, instanceType: formData.instanceType, tableName: formData.tableName}
    } else if(selectedOption === "DynamoDB"){
      values = {...values, rcu: Number(formData.rcu), wcu: Number(formData.wcu)}
    } else{
      values = {};
    };

    if(currentStep === 3){
      values = {...values, demo: "demo"};
      console.log(values);
    }


  };
  return (
    <div
      onClick={onClose}
      className={`
        fixed inset-0 flex justify-center items-center transition-colors
        ${open ? "visible bg-black/20" : "invisible"} z-50 
      `}
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className={`
          bg-white rounded-lg shadow p-6 transition-all
          ${open ? "scale-100 opacity-100" : "scale-125 opacity-0"}
        `}
        style={{ maxHeight: "80vh", overflow: "auto" }}
      >
        <button
          onClick={handleCancel}
          className="absolute top-5 right-2 p-1 rounded-lg text-gray-400 bg-white hover:bg-gray-50 hover:text-gray-600"
        >
          <MdClose size={25} />
        </button>
        <div className="w-[300px] md:w-[460px] flex flex-col">
          <div className="">
            <h3 className="text-[20px] font-bold">Create new database</h3>
          </div>
          <div className="w-[95%] h-[1px] bg-[#fcf8f8] my-6"></div>
          <form onSubmit={handleAddDatabase}>
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
                    onChange={(e) => setFormData({...formData, name: e.target.value})}
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
                        onChange={(e) => setFormData({...formData, nodeType: e.target.value})}
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
                        value={formData.cacheNodes}
                        onChange={(e) => setFormData({...formData, cacheNodes: e.target.value})}
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
                        onChange={(e) => setFormData({...formData, instanceClass: e.target.value})}
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
                        onChange={(e) => setFormData({...formData, collectionName: e.target.value})}
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
                        onChange={(e) => setFormData({...formData, instanceType: e.target.value})}
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
                        onChange={(e) => setFormData({...formData, tableName: e.target.value})}
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
                        onChange={(e) => setFormData({...formData, rcu: e.target.value})}
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
                        onChange={(e) => setFormData({...formData, wcu: e.target.value})}
                        min={1}
                        max={2}
                      />
                    </div>
                  </div>
                )}
              </>
            )}
            <div className="flex justify-center gap-3 mt-8">
              <div className={`bg-[#E5227A] w-[10px] h-[10px] rounded-full ${currentStep === 1 ? "opacity-100" : "opacity-40"}`}></div>
              <div className={`bg-[#E5227A] w-[10px] h-[10px] rounded-full ${currentStep === 2 ? "opacity-100" : "opacity-40"}`}></div>
              <div className={`bg-[#E5227A] w-[10px] h-[10px] rounded-full ${currentStep === 3 ? "opacity-100" : "opacity-40"}`}></div>
              
            </div>
            <div className="mt-3 mb-6 flex justify-end gap-2">
              <button onClick={handleCancel} type="button" className="px-3 py-1 text-[14px] font-semibold border-2 border-gray-300 rounded ">
                Cancel
              </button>
              {
                currentStep < 3 ? (
                  <button
                type="button"
                onClick={handleCurrentStep}
                className="btn bg-[#E5227A] btn-sm border border-[#E5227A] rounded text-white hover:bg-[#6b3b51d2]"
              >
                Next
              </button>
                ) : (
                  <button
                type="submit"
                onClick={handleCurrentStep}
                className="btn bg-[#E5227A] btn-sm border border-[#E5227A] rounded text-white hover:bg-[#6b3b51d2]"
              >
                Save
              </button>
                )
              }
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AddDatabaseModal;
