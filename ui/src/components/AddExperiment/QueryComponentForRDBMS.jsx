/* eslint-disable no-unused-vars */
import React, { useState, useEffect } from "react";
import TextBox from "../Common/TextBox";
import Select from "react-select";

const QueryComponentForRDBMS = ({
  sqlPlaceholder,
  selectedOption,
  columns,
  setColumns,
  handleAddColumn,
  setDbQuery,
  datasetColumnsOptions,
}) => {
  const [texts, setTexts] = useState(sqlPlaceholder);
  const [projectedFields, setProjectedFields] = useState([]);

  useEffect(() => {
    setTexts(sqlPlaceholder);
  }, [sqlPlaceholder]);

  const onChangeSQLPlaceholder = (newValue) => {
    setTexts(newValue);
  };

  useEffect(() => {
    if (selectedOption === "sql") {
      setDbQuery({ sql: texts, type: "sql" });
    } else {
      setDbQuery({
        filtersOpt: columns.map((filter) => ({
          field: filter.key,
          fieldValue: filter.value,
        })),
        projectedFieldsOpt: projectedFields.map((field) => field.value),
        tableName: "table_name", // You might want to make this dynamic
        type: "sql-builder",
      });
    }
  }, [selectedOption, texts, projectedFields, columns, setDbQuery]);

  const handleProjectedFieldsChange = (selectedOptions) => {
    setProjectedFields(selectedOptions);
  };

  const handleFilterKeyChange = (selectedOption, index) => {
    const newColumns = [...columns];
    newColumns[index] = { ...newColumns[index], key: selectedOption.value };
    setColumns(newColumns);
    console.log("Updated Columns: ", newColumns);
  };

  const handleFilterValueChange = (event, index) => {
    const newFilters = [...columns];
    newFilters[index] = { ...newFilters[index], value: event.target.value };
    setColumns(newFilters);
    console.log("Updated Filters: ", newFilters);
  };

  return (
    <div>
      {selectedOption === "sql" ? (
        <TextBox texts={texts} onChange={onChangeSQLPlaceholder} />
      ) : (
        <div>
          <div>
            <p className="my-2">select</p>
            <Select
              isMulti
              name="columns"
              options={datasetColumnsOptions}
              onChange={handleProjectedFieldsChange}
              className="basic-multi-select"
              classNamePrefix="select"
              styles={{
                container: (provided) => ({
                  ...provided,
                  width: "350px", // Set your desired width here
                }),
                control: (base) => ({
                  ...base,
                  fontSize: "13px",
                }),
                menu: (base) => ({
                  ...base,
                  fontSize: "13px",
                }),
              }}
            />
            <p className="my-2">where</p>
          </div>
          <div className="p-3 w-full md:w-[60%] border-2 border-gray-300 rounded-md">
            <div className="flex gap-3 mb-3">
              <p className="px-3 py-1 bg-accent font-medium text-gray-500 rounded-md ">And</p>
              <button
                className="btn bg-primary btn-sm text-white"
                type="button"
                onClick={handleAddColumn}
              >
                +Rule
              </button>
            </div>
            {columns.map((column, i) => (
              <div key={i} className="flex items-center gap-6">
                <div className="flex flex-col">
                  <label className="text-[12px] font-bold mb-1">Key</label>
                  <Select
                    options={datasetColumnsOptions}
                    onChange={(selectedOption) =>
                      handleFilterKeyChange(selectedOption, i)
                    }
                    styles={{
                      container: (provided) => ({
                        ...provided,
                        width: "250px", // Set your desired width here
                      }),
                      control: (base) => ({
                        ...base,
                        fontSize: "13px",
                      }),
                      menu: (base) => ({
                        ...base,
                        fontSize: "13px",
                      }),
                    }}
                  />
                </div>
                <div className="flex flex-col">
                  <label className="text-[12px] font-bold">Value</label>
                  <input
                    className="search-input mt-1"
                    type="text"
                    value={column.value}
                    onChange={(e) => handleFilterValueChange(e, i)}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default QueryComponentForRDBMS;
