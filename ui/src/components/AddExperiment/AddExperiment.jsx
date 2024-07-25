import { useState } from "react";
import { FaPlus } from "react-icons/fa6";
import { Link, useNavigate } from "react-router-dom";
import { ImPencil } from "react-icons/im";
import { IoMdClose } from "react-icons/io";
import CustomSelectMultipleOptions from "../CustomSelect/CustomSelectMultipleOptions";
import AddExperimentInputFields from "./AddExperimentInputFields";
import AddDatabaseModalForExperiment from "../Modals/AddDatabaseModalForExperiment";
import handleAddExperiment from "../../api/handleAddExperiment";

const AddExperiment = ({
  databases,
  databasesOptions,
  dataset,
  databaseRefetch,
  selectedDatabaseCategory,
}) => {
  const [selectedDatabases, setSelectedDatabases] = useState([]);
  const [writeBatchSizeValue, setWriteBatchSizeValue] = useState("1000");
  const [concurrentQueries, setConcurrentQueries] = useState("1000");
  const [experimentTimeInSecond, setExperimentTimeInSecond] = useState("60");
  const [openAddDatabaseModal, setOpenAddDatabaseModal] = useState(false);

  const navigate = useNavigate();

  const handleRemoveSelectedDatabase = (databaseToRemove) => {
    setSelectedDatabases(selectedDatabases.filter(db => db.value !== databaseToRemove.value));
  };

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

  // useEffect(() => {
  //   if (
  //     selectedDatabaseCategory.value !== "Choose Category" &&
  //     databaseCategories
  //   ) {
  //     const categoryDatabases =
  //       databaseCategories[selectedDatabaseCategory.value] || [];
  //     const filteredDatabases = databases.filter((db) =>
  //       categoryDatabases.includes(db.dataStore)
  //     );
  //     setAvailableDatabases(filteredDatabases);

  //     // Only filter selected databases if the category has changed
  //     setSelectedDatabases((prevSelected) => {
  //       const newSelected = prevSelected.filter((db) =>
  //         filteredDatabases.some(
  //           (availableDb) => availableDb.databaseConfigId.id === db.value
  //         )
  //       );

  //       // If the new selection is different, return it. Otherwise, keep the previous selection.
  //       return newSelected.length !== prevSelected.length
  //         ? newSelected
  //         : prevSelected;
  //     });
  //   } else {
  //     setAvailableDatabases([]);
  //     setSelectedDatabases([]);
  //   }
  // }, [selectedDatabaseCategory, databaseCategories, databases]);


  const paramsForInputFieldsComponent = {
    experimentTimeInSecond,
    setExperimentTimeInSecond,
    concurrentQueries,
    setConcurrentQueries,
    writeBatchSizeValue,
    setWriteBatchSizeValue,
    selectedDatabaseCategory
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
                  className={` h-full px-4  mr-2 flex gap-2 items-center justify-center bg-secondary rounded-2xl`}
                >
                  <p>{database.option}</p>
                  <ImPencil size={12} />
                  <IoMdClose
                    title="Remove Database"
                    cursor={"pointer"}
                    size={18}
                    onClick={() => handleRemoveSelectedDatabase(database)}
                  />
                </div>
              ))}
            </div>
          )}

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
