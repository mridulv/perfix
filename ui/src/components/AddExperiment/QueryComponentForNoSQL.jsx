import { useEffect } from "react";
import Select from "react-select";

const QueryComponentForNoSQL = ({
  columns,
  setColumns,
  handleAddColumn,
  datasetColumnsOptions,
  setDbQuery
}) => {
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

  useEffect(() => {
    const filters = columns
      .filter((column) => column.key && column.value)
      .map((column) => ({
        field: column.key,
        fieldValue: column.value,
      }));

    setDbQuery({
      filters: filters,
      type: "nosql",
    });
  }, [columns, setDbQuery]);
  return (
    <div className="">
      <label className="text-[12px] font-bold mb-1">Query</label>
      <div className="mt-2 p-3 w-full md:w-[60%] border-2 border-gray-300 rounded-md">
        <div className="flex gap-3 mb-3">
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
              <label className="text-[12px] font-bold">Key</label>
              <Select
                options={datasetColumnsOptions}
                onChange={(selectedOption) =>
                  handleFilterKeyChange(selectedOption, i)
                }
                styles={{
                  container: (provided) => ({
                    ...provided,
                    width: "250px",
                    marginTop: "4px",
                  }),
                  control: (base) => ({
                    ...base,
                    fontSize: "14px",
                  }),
                  menu: (base) => ({
                    ...base,
                    fontSize: "14px",
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
  );
};

export default QueryComponentForNoSQL;
