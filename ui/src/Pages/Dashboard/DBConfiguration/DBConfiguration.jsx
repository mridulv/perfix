import React from 'react';
import AddButton from '../../../components/AddButton';
import CommonTable from '../../../components/CommonTable';
import { useQuery } from 'react-query';
import axios from 'axios';
import Loading from '../../../components/Loading';

const DBConfiguration = () => {

  const {data: databases, isLoading} = useQuery({
    queryKey: ["databases"],
    queryFn: async() => {
      const res = await axios.get(`${process.env.REACT_APP_BASE_URL}/config`);
      const data = await res.data;
      return data;
    }
  })

  if(isLoading) return <Loading/>
  return (
    <div className='ps-7 pt-7'>
       <div>
        <h3 className="text-2xl font-semibold">Database</h3>
      </div>
      <div className="flex justify-between me-9 mt-6 mb-3">
        <select className="select-type w-[90px] px-2 py-2 border-2 border-gray-300 rounded-2xl text-gray-900 text-sm focus:ring-gray-500 focus:border-gray-500 ">
          <option className="">Owner</option>
          <option className="">Owner</option>
          <option className="">Owner</option>
        </select>
        <AddButton value={"New Database"} link={"/add-dataset"} />
      </div>
      <div className='me-9'>
        <CommonTable data={databases} tableHead={"Database"}/>
      </div>
    </div>
  );
};

export default DBConfiguration;