import React from 'react';
import { FaPlus } from 'react-icons/fa6';

const AddDataset = ({columns, handleAddColumn}) => {

  // const handleAddDataset = async (event) => {
  //   event.preventDefault();
  //   const datasetName = event.target.datasetName.value;
  //   const isDuplicateName = datasets.some(
  //     (dataset) => dataset.name.toLowerCase() === datasetName.toLowerCase()
  //   );
  
  //   if (isDuplicateName) {
  //     toast.error(`A dataset with the name "${datasetName}" already exists.`);
  //     return;
  //   }

  //   const formData = new FormData(event.target);
  //   const columnValues = [];

  //   columns.forEach((column, index) => {
  //     const columnName = formData.get(`columnName${index}`);
  //     const columnType = formData.get(`columnType${index}`);

  //     columnValues.push({ columnName, columnType });
  //   });

  //   try {
  //     const url = "http://localhost:9000/dataset";
  //     const columnData = {
  //       rows: 10000,
  //       name: datasetName,
  //       columns: columnValues.map((columnValue) => ({
  //         columnName: columnValue.columnName,
  //         columnType: {
  //           type: columnValue.columnType,
  //           isUnique: true,
  //         },
  //       })),
  //     };
      
  //     const response = await axios.post(url, columnData, {
  //       headers: {
  //         "Content-Type": "application/json",
  //       },
  //     });
  //     console.log(response.data);
  //     if (response.status === 200) {
  //       event.target.reset();
  //       setColumns([{ columnName: "", columnType: "" }]);
  //       toast.success("Datased Added Successfully");
  //       refetch();
  //     }
  //   } catch (err) {
  //     console.log(err);
  //   }
  // };
    return (
        <>
                <div className="flex flex-col mb-6">
                  <label className="text-[12px] font-bold">Name</label>
                  <input
                    className="search-input border-2 border-gray-300 focus:border-gray-400 max-w-[250px] px-2 py-1 rounded"
                    type="text"
                    placeholder="Enter Name"
                  />
                </div>
                <div className="flex flex-col mb-5">
                  <label className="text-[12px] font-bold">Description</label>
                  <textarea
                    className="search-input border-2 border-gray-300 w-[400px] px-2 py-1 rounded resize-none"
                    type="text"
                    placeholder="Enter Name"
                    rows={2}
                  />
                </div>
                <div>
                    <h3 className="text-base font-bold mb-4">Setup Columns</h3>
                  <div className="max-w-[290px] bg-[#fcf8f8] py-3 px-6 rounded-md">
                  {columns.map((column, i) => (
                    <div className="mb-2 " key={i}>
                      <div className="flex flex-col mb-4">
                        <div className="flex justify-between">
                        <label className="text-[12px] font-bold">{i+1}. Column Name</label>
                        </div>
                        <input
                          className="search-input border-2 border-gray-300 focus:border-gray-400 max-w-[250px] px-2 py-1 rounded"
                          placeholder="Name"
                          type="text"
                          name={`columnName${i}`}
                          id={`columnName${i}`}
                        />
                      </div>
                      <div className="flex flex-col mb-3">
                        <label className="text-[12px] font-bold">{i+1}. Column Type:</label>
                        <select
                          name={`columnType${i}`}
                          id={`columnType${i}`}
                          className="block max-w-[250px] px-2 py-2 border-2 border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-gray-500"
                          style={{
                            fontSize: '14px',
                            color: '#8E8E8E',
                          }}
                        >
                          <option value="NameType">NameType</option>
                          <option value="AddressType">AddressType</option>
                        </select>
                      </div>
                      
                    </div>
                  ))}
                  </div>
                  <div className="flex mt-3">
                    <button
                      onClick={handleAddColumn}
                      className="text-[#e83f8c]  text-[12px] font-semibold flex items-center gap-3"
                    >
                        <FaPlus size={12}/>
                      Add Column
                    </button>
                  </div>
                </div>
              </>
    );
};

export default AddDataset;