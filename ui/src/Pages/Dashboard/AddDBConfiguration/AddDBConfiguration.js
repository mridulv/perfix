import axios from 'axios';
import React from 'react';
import toast from 'react-hot-toast';
import { useQuery } from 'react-query';



const stores = [
    {
      name: "MySQL",
      value: "mysql",
    },
  
    {
      name: "Redis",
      value: "redis",
    },
    {
      name: "DynamoDB",
      value: "dynamodb",
    },
    {
      name: "MongoDB",
      value: "mongodb",
    },
  ];
const AddDBConfiguration = () => {

    const {
        data: configs,
        isLoading,
        refetch,
      } = useQuery({
        queryKey: ["config"],
        queryFn: async () => {
          const res = await axios.get("http://localhost:9000/config");
          const data = await res.data;
    
          return data;
        },
      });


    const handleAddConfig = async (e) => {
        e.preventDefault();
        const storeName = e.target.storeName.value;
        const name = e.target.configurationName.value;
    
    
        const isDuplicate = configs.some((config) => config.name.toLowerCase() === name.toLowerCase());
        if(isDuplicate){
          toast.error(`A config with the name "${name}" already exists.`);
          return;
        }
    
        try {
          const res = await axios.post("http://localhost:9000/config", {
            storeName,
            name
          });
          
          if (res.status === 200) {
            refetch();
            e.target.reset();
          }
        } catch (err) {
          console.log(err);
        }
      };
    return (
        <div>
            <div className="min-h-screen flex flex-col justify-center items-center">
      <form onSubmit={handleAddConfig}>
        <label className="form-control w-full max-w-xs mb-4">
          <div className="label">
            <span className="label-text text-base">
              Enter the name of the configuration.
            </span>
          </div>
          <input
            type="text"
            className="input input-bordered w-full max-w-xs"
            style={{ outline: "none" }}
            required
            name="configurationName"
            placeholder="Configuration Name"
          />
        </label>
        <label className="form-control w-full max-w-xs">
          <div className="label">
            <span className="label-text">Store Name</span>
          </div>
          <select name="storeName" className="select select-bordered">
            {stores.map((store) => (
              <option value={store.value} key={store.value}>
                {store.name}
              </option>
            ))}
          </select>
        </label>
        <div className="my-4 flex justify-center">
          <input
            className="btn btn-error text-white btn-md"
            type="submit"
            value={"Add"}
          />
        </div>
      </form>
      </div>
        </div>
    );
};

export default AddDBConfiguration;