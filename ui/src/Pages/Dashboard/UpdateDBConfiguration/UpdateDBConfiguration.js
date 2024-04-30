import axios from 'axios';
import React from 'react';
import toast from 'react-hot-toast';
import { useQuery } from 'react-query';
import { useNavigate, useParams } from 'react-router-dom';
import Loading from '../../../components/Loading';



const UpdateDBConfiguration = () => {
    const {id} = useParams();
    const navigate = useNavigate();

    
    const {data: config, isLoading: configLoading} = useQuery({
        queryKey: ["config", id],
        queryFn: async () => {
            const res = await axios.get(`http://localhost:9000/config/${id}`);
            const data = await res.data;
            return data;
        },
    });




  

    const {
        data: configs,
        isLoading
      } = useQuery({
        queryKey: ["configs"],
        queryFn: async () => {
          const res = await axios.get("http://localhost:9000/config");
          const data = await res.data;
    
          return data;
        },
      });


    const handleUpdate = async (e) => {
        e.preventDefault();
        const name = e.target.configurationName.value;
    
    
        if(name !== config.name){
            const isDuplicate = configs.some((config) => config.name.toLowerCase() === name.toLowerCase());
        if(isDuplicate){
          toast.error(`A config with the name "${name}" already exists.`);
          return;
        }
        }
    
        try {
          const res = await axios.post(`http://localhost:9000/config/${id}`, {
            name,
            storeName: config.storeName
          });
          console.log(res);
          if (res.status === 200) {
            toast.success("Configuration update started successfully");
            e.target.reset();
            navigate(`/update-input-configuration/${id}`)
          }
        } catch (err) {
          console.log(err);
        }
      };
      if(isLoading && configLoading) return <Loading/>;
    return (
        <div>
            <div className="min-h-screen flex flex-col justify-center items-center">
        <h3 className='text-center text-xl font-bold my-2 underline'>Update Configuration</h3>
      <form onSubmit={handleUpdate}>
        <label className="form-control w-full max-w-xs mb-4">
          <div className="label">
            <span className="label-text text-base">
              Name of the configuration.
            </span>
          </div>
          <input
            type="text"
            className="input input-bordered w-full max-w-xs"
            style={{ outline: "none" }}
            required
            name="configurationName"
            placeholder="Configuration Name"
            defaultValue={config?.name}
          />
        </label>
        <div className="my-4 flex justify-center">
          <input
            className="btn btn-error text-white btn-md"
            type="submit"
            value={"Next"}
          />
        </div>
      </form>
      </div>
        </div>
    );
};

export default UpdateDBConfiguration;