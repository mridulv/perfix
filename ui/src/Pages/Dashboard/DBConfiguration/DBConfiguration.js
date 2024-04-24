import axios from "axios";
import React, { useEffect } from "react";
import { useQuery } from "react-query";
import { Link } from "react-router-dom";





const DBConfiguration = () => {
  

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

  if (isLoading) return <p>Loading...</p>;
  return (
      <div>
      <h3 className="text-lg font-bold text-center">Configurations</h3>
      <div className="flex justify-end pe-12">
        
          <Link
            className="btn btn-primary btn-md text-white my-4"
            to="/add-db-configuration"
          >
            Add Configuration
          </Link>
        
      </div>
      <div className="mt-4">
        <div className="grid grid-cols-3 gap-3">
          {configs.map((config) => (
            <div
              className="p-4 border border-gray-400 shadow-md my-3"
              key={config.databaseConfigId.id}
            >
              <p>Configuration Name: {config.name}</p>
              <p> store name: {config.storeName}</p>
              <Link
                className="btn btn-error btn-sm text-white my-4"
                to={`/input-configuration/${config.databaseConfigId.id}`}
              >
                Submit inputs
              </Link>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default DBConfiguration;
