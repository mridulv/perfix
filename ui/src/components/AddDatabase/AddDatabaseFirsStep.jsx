import React, { useEffect, useState } from "react";
import CustomSelect from "../CustomSelect/CustomSelect";

const AddDatabaseFirsStep = ({
  setDatabaseName,
  selectedDatabaseType,
  setSelectedDatabaseType,
  databaseCategoriesOptions,
  selectedDatabaseCategory,
  setSelectedDatabaseCategory,
  databaseCategories,
}) => {
  const [databaseTypesOptions, setDatabaseTypesOptions] = useState([]);

  useEffect(() => {
    if (selectedDatabaseCategory?.value !== "Choose") {
      const types =
        databaseCategories &&
        databaseCategories[selectedDatabaseCategory.value];
      const options = types.map((type) => ({ option: type, value: type }));
      setDatabaseTypesOptions(options);
    } else {
      setDatabaseTypesOptions([]);
    }
    // Reset selected database type when the category changes
    setSelectedDatabaseType({ option: "Choose Database", value: "Choose" });
  }, [databaseCategories, selectedDatabaseCategory, setSelectedDatabaseType]);

  return (
    <div>
      <div className="flex flex-col mb-4">
        <label className="text-[12px] font-bold mb-[2px]">Name</label>
        <input
          className=" border-2 border-gray-300 focus:border-gray-400  max-w-[250px] px-2 py-2 rounded"
          placeholder="Name"
          name="name"
          type="text"
          required
          onChange={(e) => setDatabaseName(e.target.value)}
        />
      </div>
      <div className="flex flex-col mb-4">
        <label className="text-[12px] font-bold mb-[2px]">
          Database Category
        </label>
        <div className="w-[350px]">
          <CustomSelect
            options={databaseCategoriesOptions}
            selected={selectedDatabaseCategory}
            setSelected={setSelectedDatabaseCategory}
            width="w-[250px]"
          />
        </div>
      </div>
      <div className="flex flex-col mb-2">
        <label className="text-[12px] font-bold mb-[2px]">Database type</label>
        <div className="w-[350px]">
          <CustomSelect
            options={databaseTypesOptions}
            selected={selectedDatabaseType}
            setSelected={setSelectedDatabaseType}
            width="w-[250px]"
          />
        </div>
      </div>
    </div>
  );
};

export default AddDatabaseFirsStep;
