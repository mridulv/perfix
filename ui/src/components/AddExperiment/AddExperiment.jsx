import React, { useEffect, useState } from "react";
import { FaPlus } from "react-icons/fa6";
import { Link, useNavigate } from "react-router-dom";
import { ImPencil } from "react-icons/im";
import { IoMdClose } from "react-icons/io";
import CustomSelectMultipleOptions from "../CustomSelect/CustomSelectMultipleOptions";
import AddExperimentInputFields from "./AddExperimentInputFields";
import AddDatabaseModalForExperiment from "../Modals/AddDatabaseModalForExperiment";
import handleAddExperiment from "../../api/handleAddExperiment";
import fetchDatabaseCategory from "../../api/fetchDatabaseCategory";
import CustomSelect from "../CustomSelect/CustomSelect";

const AddExperiment = ({ databases, dataset, databaseRefetch }) => {
  const [selectedDatabases, setSelectedDatabases] = useState([]);
  const [databaseCategories, setDatabaseCategories] = useState(null);
  const [selectedDatabaseCategory, setSelectedDatabaseCategory] = useState({
    option: "Choose Category",
    value: "Choose"
  })
  const [writeBatchSizeValue, setWriteBatchSizeValue] = useState(0);
  const [concurrentQueries, setConcurrentQueries] = useState(0);
  const [experimentTimeInSecond, setExperimentTimeInSecond] = useState(0);
  const [openAddDatabaseModal, setOpenAddDatabaseModal] = useState(false);
  const [availableDatabases, setAvailableDatabases] = useState([]);

  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();

    const form = event.target;

    await handleAddExperiment(
      form,
      writeBatchSizeValue,
      concurrentQueries,
      experimentTimeInSecond,
      selectedDatabases,
      navigate
    );
  };

  useEffect(() => {
    fetchDatabaseCategory(setDatabaseCategories);
  }, []);

  useEffect(() => {
    if (selectedDatabaseCategory.value !== "Choose Category" && databaseCategories) {
      const categoryDatabases = databaseCategories[selectedDatabaseCategory.value] || [];
      const filteredDatabases = databases.filter(db => categoryDatabases.includes(db.dataStore));
      setAvailableDatabases(filteredDatabases);

      // Only filter selected databases if the category has changed
      setSelectedDatabases(prevSelected => {
        const newSelected = prevSelected.filter(db => 
          filteredDatabases.some(availableDb => availableDb.databaseConfigId.id === db.value)
        );
        
        // If the new selection is different, return it. Otherwise, keep the previous selection.
        return newSelected.length !== prevSelected.length ? newSelected : prevSelected;
      });
    } else {
      setAvailableDatabases([]);
      setSelectedDatabases([]);
    }
  }, [selectedDatabaseCategory, databaseCategories, databases]);

  const databaseCategoriesOptions =
    databaseCategories &&
    Object.keys(databaseCategories).map((category) => ({
      option: category,
      value: category,
    }));

    const databasesOptions = availableDatabases.map((database) => ({
      option: database.name,
      value: database.databaseConfigId.id,
    }));

  const paramsForInputFieldsComponent = {
    experimentTimeInSecond,
    setExperimentTimeInSecond,
    concurrentQueries,
    setConcurrentQueries,
    writeBatchSizeValue,
    setWriteBatchSizeValue,
  };
  return (
    <div>
      <div className="ps-7 mt-6">
        <p className="text-[12px] font-bold mb-[2px] block">Choose Database</p>
        <div className="flex items-center gap-3">
          {selectedDatabases.length > 0 && (
            <div
              className={`min-w-[140px] h-[35px] flex items-center justify-center rounded-2xl text-[12px]
                font-semibold `}
            >
              {selectedDatabases.map((database) => (
                <div
                  key={database.value}
                  className={`w-full h-full px-4  mr-2 flex gap-2 items-center justify-center bg-secondary rounded-2xl`}
                >
                  <p>{database.option}</p>
                  <ImPencil size={12} />
                  <IoMdClose
                    title="Remove Database"
                    cursor={"pointer"}
                    size={18}
                  />
                </div>
              ))}
            </div>
          )}
          <div>
            <CustomSelect
              options={databaseCategoriesOptions}
              selected={selectedDatabaseCategory}
              setSelected={setSelectedDatabaseCategory}
              width="w-[200px]"
            />
          </div>
          <div>
            <CustomSelectMultipleOptions
              selected={selectedDatabases}
              setSelected={setSelectedDatabases}
              options={databasesOptions}
              width="w-[200px]"
              name="Databases"
            />
          </div>
          <div>
            <button
              onClick={() => setOpenAddDatabaseModal(true)}
              className="px-2 flex items-center gap-2 text-primary text-[12px] rounded  font-semibold"
            >
              <FaPlus />
              Add new database
            </button>
          </div>
          <AddDatabaseModalForExperiment
            open={openAddDatabaseModal}
            onClose={() => setOpenAddDatabaseModal(false)}
            databases={databases}
            seletedDataset={dataset}
            refetch={databaseRefetch}
          />
        </div>
      </div>

      <div className="ps-7 mt-7">
        <form onSubmit={handleSubmit}>
          <AddExperimentInputFields params={paramsForInputFieldsComponent} />

          <div className="mt-[100px] pb-3 flex gap-2">
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

export default AddExperiment;
