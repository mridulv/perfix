const QueryComponentForNoSQL = ({ columns, handleAddColumn }) => {
  return (
    <div>
      <label className="text-[12px] font-bold mb-1">Query</label>

      <div>
        <p className="my-1">select</p>
        <select className="w-[250px] px-1 py-[6px]  border border-gray-300 rounded-md">
          <option className="" value="1">1</option>
          <option value="2">2</option>
          <option value="3">3</option>
        </select>
        <p className="my-1">where</p>
      </div>
      <div className="p-3 w-full md:w-[60%] border-2 border-gray-300 rounded-md">
        <div className="flex gap-3">
          <p className="px-3 py-1 bg-accent rounded-md ">And</p>
          <button className="btn bg-primary btn-sm text-white" type="button" onClick={handleAddColumn}>+Rule</button>
        </div>
        {columns.map((column, i) => (
          <div key={i} className="flex items-center gap-4">
            <div className="flex flex-col">
              <label className="text-[12px] font-bold">Key</label>
              <select className="w-[250px] px-1 py-[6px]  border border-gray-300 rounded-md">
                <option value="1">1</option>
                <option value="2">2</option>
                <option value="3">3</option>
              </select>
            </div>
            <div className="flex flex-col">
              <label className="text-[12px] font-bold">Value</label>
              <input className="search-input mt-1" type="text" />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default QueryComponentForNoSQL;
