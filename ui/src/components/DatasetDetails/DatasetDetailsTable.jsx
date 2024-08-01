const DatasetDetailsTable = ({ data }) => {
  console.log("Table data:", data);
  const limitedColumnDatas = data?.data.slice(0, 10);
  const columnNames = limitedColumnDatas.length > 0 ? Object.keys(limitedColumnDatas[0]) : [];

  return (
    <div className="pt-5">
      <table className="w-full rounded-t-xl">
        <thead className="">
          <tr className="border-b border-[#E0E0E0]">
            {columnNames.map((columnName) => (
              <th key={columnName} className="w-[50%] text-start text-[#333E48] font-semibold text-sm pb-3 px-2">
                {columnName}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {limitedColumnDatas.map((row, rowIndex) => (
            <tr key={rowIndex} className="border-b border-[#E0E0E0]">
              {columnNames.map((columnName) => (
                <td key={columnName} className="text-xs py-3 px-2">
                  {row[columnName]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default DatasetDetailsTable;