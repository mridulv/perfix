import axios from "axios";
import React, { useEffect, useState } from "react";
import toast from "react-hot-toast";
import AddDatabaseInputFields from "./AddDatabaseInputFields";
import NavigationStep from "./NavigationStep";
import CustomSelect from "../CustomSelect";
import Loading from "../Loading";

const AddDatabase = ({
  dataset,
  onClose = null,
  navigate = null,
  valueFor,
  setSelectedDatabase = null,
  refetch = null,
}) => {
  const [currentStep, setCurrentStep] = useState(1);
  const [maxSteps, setMaxSteps] = useState(2);

  const [databaseName, setDatabaseName] = useState("")
  const [databaseTypes, setDatabaseTypes] = useState(null);
  const [selectedDatabaseType, setselectedDatabaseType] = useState({
    option: "Choose Database",
    value: "Choose",
  });
  const [inputFields, setInputFields] = useState([]);
  const [isInputFieldsLoading, setIsInputFieldsLoading] = useState(false);
  const [inputValues, setInputValues] = useState({});

  // fetch the database types
  useEffect(() => {
    const fetchDatabaseTypes = async () => {
      const res = await axios.get(
        `${process.env.REACT_APP_BASE_URL}/databases`,
        {
          withCredentials: true,
        }
      );
      const data = await res.data;

      if (res.status === 200) {
        setDatabaseTypes(data);
      }
    };
    fetchDatabaseTypes();
  }, []);

  // setting the database types in options format for custom select
  let databaseTypesOptions = [];
  if (databaseTypes) {
    databaseTypesOptions = databaseTypes?.map((database) => ({
      option: database,
      value: database,
    }));
  }

  const handleCurrentStep = () => {
    setCurrentStep(currentStep + 1);
  };

  const getCurrentStep = (currentStep) => {
    return currentStep - 2;
  };

  //after the database type selected and in second step we will get the correct input fields
  useEffect(() => {
    if (currentStep === 2 && selectedDatabaseType.value !== "Choose") {
      setIsInputFieldsLoading(true);
      const fetchInputFields = async () => {
        const res = await axios.get(
          `${process.env.REACT_APP_BASE_URL}/databases/inputs?databaseType=${selectedDatabaseType.value}`,
          {
            withCredentials: true,
          }
        );
        if (res.status === 200) {
          setInputFields(res.data.forms);
          setMaxSteps(res.data.forms.length + 1);
          setIsInputFieldsLoading(false);
        }
      };
      fetchInputFields();
    }
  }, [selectedDatabaseType, currentStep]);

  // close the modal
  const handleCancel = () => {
    if (valueFor === "modal") {
      onClose();
    } else if (valueFor === "page") {
      navigate("/database");
    }
    setCurrentStep(1);
  };


  //add database submit function
  const handleAddDatabase = async (e) => {
    e.preventDefault();
   

    const values = {
      name: databaseName && databaseName,
      dataStore:
        selectedDatabaseType.value === "MySQL"
          ? "MySQLStoreType"
          : selectedDatabaseType.value === "Redis"
          ? "RedisStoreType"
          : selectedDatabaseType.value === "DocumentDB"
          ? "MongoDBStoreType"
          : "DynamoDBStoreType",
      datasetId: { id: dataset.id.id },
      storeParams: {...inputValues, type: selectedDatabaseType.value},
    };

    console.log(inputValues);
    if (
      currentStep > 2 &&
      currentStep === maxSteps &&
      Object.keys(inputValues).length > 0
    ) {
      try {
        const res = await axios.post(
          `${process.env.REACT_APP_BASE_URL}/config/create`,
          values,
          {
            withCredentials: true,
          }
        );
        if (res.status === 200) {
          toast.success("Database added successfully!");

          handleCancel();
        } else {
          toast.error("Failed to add database.");
        }
      } catch (error) {
        toast.error("An error occurred. Please try again.");
      }
    }
  };

  const handleInputChange = (key, value) => {
    setInputValues((prevValues) => ({
      ...prevValues,
      [key]: value,
    }));
  };
  return (
    <div className="ps-7 flex flex-col relative">
      <form className="" onSubmit={handleAddDatabase}>
        <div>
          {/* step-1 */}
          <div>
            {currentStep === 1 && (
              <div>
                <div className="flex flex-col mb-4">
                  <label className="text-[12px] font-bold mb-[2px]">Name</label>
                  <input
                    className="search-input border-2 border-gray-300 focus:border-gray-400 outline-pink-600 max-w-[250px] px-2 py-2 rounded"
                    placeholder="Name"
                    name="name"
                    type="text"
                    required
                    onChange={(e) => setDatabaseName(e.target.value)}
                  />
                </div>
                <div className="flex flex-col mb-2">
                  <label className="text-[12px] font-bold mb-[2px]">
                    Database type
                  </label>
                  <div className="w-[350px]">
                    <CustomSelect
                      options={databaseTypesOptions}
                      selected={selectedDatabaseType}
                      setSelected={setselectedDatabaseType}
                      width="w-[250px]"
                    />
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* after step-1 we are showing the input fields dynamically */}
          <div>
            {isInputFieldsLoading ? (
              <Loading />
            ) : inputFields.length > 0 && currentStep > 1 ? (
              inputFields?.map((inputSet, index) => (
                <div key={index}>
                  {getCurrentStep(currentStep) === index && (
                    <div>
                      {inputSet.inputs &&
                        Object.entries(inputSet.inputs).map(([key, value]) => (
                          <AddDatabaseInputFields
                            key={key}
                            input={{ [key]: value }}
                            handleInputChange={handleInputChange}
                          />
                        ))}
                    </div>
                  )}
                </div>
              ))
            ) : null}
          </div>
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

          <div>
            <NavigationStep
              currentStep={currentStep}
              handleCurrentStep={handleCurrentStep}
              handleCancel={handleCancel}
              maxSteps={maxSteps}
            />
          </div>
        </div>
      </form>
    </div>
  );
};

export default AddDatabase;
