import axios from "axios";
import React, { useEffect } from "react";
import { useQuery } from "react-query";
import { Link } from "react-router-dom";

const DBConfiguration = () => {
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

    try {
      const res = await axios.post("http://localhost:9000/config", {
        storeName,
      });
      console.log(res);
      if (res.status === 200) {
        refetch();
      }
    } catch (err) {
      console.log(err);
    }
  };

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
    <div className="min-h-screen flex flex-col justify-center items-center">
      <form onSubmit={handleAddConfig}>
        <label className="form-control w-full max-w-xs">
          <div className="label">
            <span className="label-text">Store Name</span>
          </div>
          <select name="storeName" className="select select-bordered w-60">
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
      <div className="mt-4">
        <h3 className="text-lg font-bold text-center">Configurations</h3>
        <div className="grid grid-cols-3">
          {configs.map((config) => (
            <div
              className="p-4 border border-gray-400 shadow-md my-3"
              key={config.databaseConfigId.id}
            >
              <p> store name: {config.storeName}</p>
              <p>store id: {config.databaseConfigId.id}</p>
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
