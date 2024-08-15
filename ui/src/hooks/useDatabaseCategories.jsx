/* eslint-disable no-unused-vars */
import React, { useState, useEffect } from "react";
import fetchDatabaseCategory from "../api/fetchDatabaseCategory";

const useDatabaseCategories = (selectedCategory) => {
  const [databaseCategories, setDatabaseCategories] = useState(null);

  useEffect(() => {
    fetchDatabaseCategory(setDatabaseCategories);
  }, []);

  const databaseCategoriesOptions = 
    databaseCategories
      ? Object.keys(databaseCategories).map((category) => ({
          value: category,
          label: category,
        }))
      : [];

  const databaseTypesOptions = 
    databaseCategories && 
    selectedCategory && 
    selectedCategory.value && 
    databaseCategories[selectedCategory.value]
      ? (Array.isArray(databaseCategories[selectedCategory.value])
          ? databaseCategories[selectedCategory.value]
          : Object.keys(databaseCategories[selectedCategory.value])
        ).map((type) => ({
          value: type,
          label: type,
        }))
      : [];

  return {
    databaseCategories,
    databaseCategoriesOptions,
    databaseTypesOptions,
  };
};

export default useDatabaseCategories;