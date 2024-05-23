import React, {  useState } from 'react';
import AddButton from '../../../components/AddButton';
import CommonTable from '../../../components/CommonTable';
import { useQuery } from 'react-query';
import axios from 'axios';
import Loading from '../../../components/Loading';
import CustomSelect from '../../../components/CustomSelect';

const columnHeads = ["Database Name", "Database Type", "Dataset Name", "Created At"];
const databaseTypeOptions = ["MySQL", "DynamoDB", "Redis", "DocumentDB"];

const DBConfiguration = () => {
  const [selectedDatabaseType, setSelectedDatabaseType] = useState("Choose Type");

  const { data: databases, isLoading } = useQuery({
    queryKey: ["databases", selectedDatabaseType],
    queryFn: async () => {
      let values = [];
  
      if (selectedDatabaseType !== "Choose Type") {
        values = [{ store: selectedDatabaseType, type: "DatabaseTypeFilter" }];
      }
  
      const res = await axios.post(`${process.env.REACT_APP_BASE_URL}/config`, values, {
        headers: {
          "Content-Type": "application/json",
        },
      });
  
      const data = await res.data;
      return data;
    },
  });

  
  const  dataForTable = databases?.map(database => (
    {
      databaseName: database.name,
      databaseType: database.storeParams.type,
      datasetName: "Pending",
      createdAt: new Date(database.createdAt).toLocaleDateString()
    }
  ))
 


  if(isLoading) return <Loading/>
  return (
    <div className='ps-7 pt-7'>
       <div>
        <h3 className="text-2xl font-semibold">Database</h3>
      </div>
      <div className="w-[95%] h-[1px] bg-[#fcf8f8] my-6"></div>
      <div className="flex justify-between me-9 mt-6 mb-3">
      <div className="flex gap-x-4">
          <input
            className="w-[200px] px-1 py-[6px]  border-2 border-gray-200 rounded search-input"
            type="text"
            name=""
            id=""
            placeholder="Search"
          />
          <div className='ms-3'>
            <label className='text-15px'>Filter: </label>
            {/* <select className="select-type w-[90px] px-2 py-2 border-2 border-gray-300 rounded-2xl text-gray-900 text-sm focus:ring-gray-500 focus:border-gray-500 ">
              <option className="">All</option>
              <option className="">All</option>
              <option className="">All</option>
            </select> */}
            <CustomSelect selected={selectedDatabaseType} setSelected={setSelectedDatabaseType} options={databaseTypeOptions}/>
          </div>
        </div>
        <div>
        <AddButton value={"New Database"} link={"/add-database"} />
        </div>
      </div>
      <div className='me-9'>
      <div className="">
        <CommonTable data={dataForTable} tableHead={"Database"} columnHeads={columnHeads} />
      </div>
      </div>
    </div>
  );
};

export default DBConfiguration;