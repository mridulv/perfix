import React from "react";

const CommonTable = ({tableHead}) => {
  return (
    <div>
      <div className="bg-[#fcf8f8] py-2 ps-3 text-[14px] font-semibold border-b-2 border-gray-300 rounded-t-lg">
        <p>1 {tableHead}</p>
      </div>
      <table className="w-full ">
        <thead>
          <tr className="border-b border-gray-300">
            <th className="text-start w-[50%] text-[12px] text-gray-300 py-4 ps-4">
              Name
            </th>
            <th className="text-start text-[12px] text-gray-300 ">Column 1</th>
            <th className="text-start text-[12px] text-gray-300 ">Column 2</th>
            <th className="text-start text-[12px] text-gray-300 ">Column 3</th>
          </tr>
        </thead>
        <tbody>
          <tr className="border-b border-gray-300">
            <td className="py-3 text-[14px] font-semibold ps-4">
              Experiment Name
            </td>
            <td className="text-[14px]">Cell 1</td>
            <td className="text-[14px]">Cell 2</td>
            <td className="text-[14px]">Cell 3</td>
          </tr>
        </tbody>
      </table>
    </div>
  );
};

export default CommonTable;
