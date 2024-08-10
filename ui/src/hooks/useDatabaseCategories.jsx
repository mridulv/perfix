/* eslint-disable no-unused-vars */
import React, { useState, useEffect } from "react";
import fetchDatabaseCategory from "../api/fetchDatabaseCategory";

const useDatabaseCategories = (selectedCategory) => {
  const [databaseCategories, setDatabaseCategories] = useState(null);

  useEffect(() => {
    fetchDatabaseCategory(setDatabaseCategories);
  }, []);

  const databaseCategoriesOptions =
    databaseCategories &&
    Object.keys(databaseCategories).map((category) => ({
      value: category,
      label: category,
    }));

    const databaseTypesOptions =
    selectedCategory.value !== "" && databaseCategories[selectedCategory.value]
      ? databaseCategories[selectedCategory.value].map((type) => ({
          value: type,
          label: type,
        }))
      : [];
    
      console.log(databaseTypesOptions);
  return {
    databaseCategories,
    databaseCategoriesOptions,
    databaseTypesOptions,
  };
};

export default useDatabaseCategories;