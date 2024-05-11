import axios from "axios";
import React from "react";
import toast from "react-hot-toast";
import { useQuery } from "react-query";
import { useNavigate } from "react-router-dom";
import Loading from "../../../components/Loading";

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
  const navigate = useNavigate();

  const { data: configs, isLoading } = useQuery({
    queryKey: ["configs"],
    queryFn: async () => {
      const res = await axios.get(`${process.env.REACT_APP_BASE_URL}/config`);
      const data = await res.data;

      return data;
    },
  });

  const handleAddConfig = async (e) => {
    e.preventDefault();
    const storeName = e.target.storeName.value;
    const name = e.target.configurationName.value;

    const isDuplicate = configs.some(
      (config) => config.name.toLowerCase() === name.toLowerCase()
    );
    if (isDuplicate) {
      toast.error(`A config with the name "${name}" already exists.`);
      return;
    }

    try {
      const res = await axios.post("${process.env.REACT_APP_BASE_URL}/config", {
        storeName,
        name,
      });
      console.log(res.data);
      if (res.status === 200) {
        toast.success("Configuration created successfully");
        e.target.reset();
        navigate(`/input-configuration/${res.data.id}`);
      }
    } catch (err) {
      console.log(err);
    }
  };
  if (isLoading) return <Loading />;
  return (
    <div>
      <div className="min-h-screen flex flex-col justify-center items-center">
        <h3 className="text-center text-xl font-bold my-2 underline">
          Add Configuration
        </h3>
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
              value={"Next"}
            />
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddDBConfiguration;
