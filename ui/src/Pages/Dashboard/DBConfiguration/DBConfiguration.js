import axios from "axios";
import React, { useEffect } from "react";
import { useQuery } from "react-query";

const DBConfiguration = () => {
    const stores = [
        {
            name: "MySQL",
            value: "mysql"
        },
        
        {
            name: "Redis",
            value: "redis"
        },
        {
            name: "DynamoDB",
            value: "dynamodb"
        },
        {
            name: "MongoDB",
            value: "mongodb"
        },
    ];

    const handleAddConfig = async(e) => {
        e.preventDefault();
        const storeName = e.target.storeName.value;
        console.log(storeName);

        try{
            const res = await axios.post("http://localhost:9000/config", {storeName})
            console.log(res);
        }
        catch(err){
            console.log(err);
        }
    };

    const {data: configs, isLoading} = useQuery({
        queryKey: ["config"], 
        queryFn: async () => {
            const res = await axios.get("http://localhost:9000/config");
            const data = await res.data;
            
            return data;
        }
    })
    console.log(configs);


    // useEffect(() => {
    //     const fetchConfig = async() => {
    //         const id = "";
    //         const res = await axios.get(`http://localhost:9000/config/${id}`);
    //         const data = await res.data;
    //         console.log(data);
    //     };
    //     fetchConfig();
    // }, [])


    if(isLoading) return <p>Loading...</p>
  return (
    <div className="min-h-screen flex justify-center items-center">
      <form onSubmit={handleAddConfig}>
        <label className="form-control w-full max-w-xs">
          <div className="label">
            <span className="label-text">Store Name</span>
          </div>
          <select name="storeName" className="select select-bordered w-60">
            
            {
                stores.map(store => (
                    <option value={store.value} key={store.value}>{store.name}</option>
                ))
            }
          </select>
        </label>
        <div className="my-4 flex justify-center">
        <input className="btn btn-error text-white btn-md" type="submit" value={"Add"} />
        </div>
      </form>
    </div>
  );
};

export default DBConfiguration;
