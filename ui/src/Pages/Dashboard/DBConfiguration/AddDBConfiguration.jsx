import React from 'react';
import AddDatabase from '../../../components/AddDatabase';
import axios from 'axios';
import { useQuery } from 'react-query';
import { useNavigate, useParams } from 'react-router-dom';
import Loading from '../../../components/Loading';

const AddDBConfiguration = () => {
    const {datasetId} = useParams();
    const navigate = useNavigate();


    const {data: dataset, isLoading: isDatasetLoading} = useQuery({
        queryKey: ["dataset", datasetId],
        queryFn: async() => {
          const res = await axios.get(`${process.env.REACT_APP_BASE_URL}/dataset/${datasetId}`)
          const data = await res.data;
          return data;
        }
      })

    if(isDatasetLoading) return <Loading/>
    return (
        <div className='w-[300px] md:w-[460px] flex'>
            <AddDatabase dataset={dataset} valueFor={"page"} navigate={navigate}/>
        </div>
    );
};

export default AddDBConfiguration;