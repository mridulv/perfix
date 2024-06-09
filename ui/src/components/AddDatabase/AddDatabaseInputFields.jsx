const AddDatabaseInputFields = ({ input, handleInputChange }) => {

  const key = Object.keys(input)[0];
  const { dataType, isRequired } = input[key];

  const handleChange = (e) => {
    handleInputChange(key, e.target.value);
  };

  return (
    <div className="flex flex-col mb-4">
      <label className="text-[12px] font-bold mb-[2px]">
        {Object.keys(input)[0]}
      </label>
      <div>
        {dataType === "StringType" && (
          <input
            type="text"
            className="border border-black w-[250px] px-2 py-1"
            name={key}
            required={isRequired}
            onChange={handleChange}
          />
        )}
        {dataType === "IntType" && (
          <input
            className="search-input border-2 border-gray-300 focus:border-gray-400 outline-pink-600 max-w-[250px] px-2 py-2 rounded"
            placeholder={Object.keys(input)[0]}
            type="number"
            name={key}
            required={isRequired}
            onChange={handleChange}
          />
        )}
      </div>
    </div>
  );
};

export default AddDatabaseInputFields;
