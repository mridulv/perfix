/* eslint-disable no-unused-vars */
import React from "react";
import StyledReactSelect from "../CustomSelect/StyledReactSelect";

const DatabaseInfoStep = ({
  databaseName,
  setDatabaseName,
  selectedCategory,
  setSelectedCategory,
  selectedDatabaseType,
  setSelectedDatabaseType,
  databaseCategoriesOptions,
  databaseTypesOptions,
  isUpdate,
}) => (
  <div className="min-h-[350px]">
    <div className="mb-2 flex flex-col">
      <label className="text-[12px] font-bold mb-[2px]">Database Name</label>
      <input
        type="text"
        value={databaseName}
        onChange={(e) => setDatabaseName(e.target.value)}
        className="search-input border-2 border-gray-300 focus:border-gray-400 max-w-[250px] px-2 py-2 rounded"
      />
    </div>

    <div className="mb-2">
      <label className="text-[12px] font-bold mb-[2px]">Database Category</label>
      <StyledReactSelect
        value={selectedCategory}
        onChange={(newCategory) => {
          setSelectedCategory(newCategory);
          setSelectedDatabaseType({ value: "", label: "Select a Database Type" });
        }}
        options={databaseCategoriesOptions}
        isDisabled={isUpdate}
      />
    </div>

    {selectedCategory.value !== "" && (
      <div className="mb-2">
        <label className="text-[12px] font-bold mb-[2px]">Database Type</label>
        <StyledReactSelect
          value={selectedDatabaseType}
          onChange={setSelectedDatabaseType}
          options={databaseTypesOptions}
          isDisabled={isUpdate}
        />
      </div>
    )}
  </div>
);

export default DatabaseInfoStep;