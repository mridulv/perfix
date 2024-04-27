import axios from "axios";
import React, { useState } from "react";
import { useQuery } from "react-query";
import { Link } from "react-router-dom";
import { SlOptionsVertical } from "react-icons/sl";
import Loading from "../../../components/Loading";
import ConfirmationModal from "../../../components/ConfirmationModal";
import toast from "react-hot-toast";





const DBConfiguration = () => {
  const [showOptions, setShowOptions] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedConfig, setSelectedConfig] = useState(null);

  const {
    data: configs,refetch,
    isLoading,
  } = useQuery({
    queryKey: ["config"],
    queryFn: async () => {
      const res = await axios.get("http://localhost:9000/config");
      const data = await res.data;

      return data;
    },
  });

  const handleShowOptions = (config) => {
    setShowOptions(showOptions === config ? null : config);
    setSelectedConfig(config);
  };


  const handleDeleteConfig = async (id) => {
  
    const res = await axios.delete(`http://localhost:9000/config/${id}`);
    console.log(res);
    if (res.status === 200) {
      toast.success("Config successfully deleted.")
      refetch();
      setIsModalOpen(false);
    }
  };
  

  // useEffect(() => {
  //     const fetchConfig = async() => {
  //         const id = "";
  //         const res = await axios.get(`http://localhost:9000/config/${id}`);
  //         const data = await res.data;
  //         console.log(data);
  //     };
  //     fetchConfig();
  // }, [])

  if (isLoading) return <Loading/>;
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
        <div className="w-[90%] mx-auto grid grid-cols-3 gap-3">
          {configs.map((config) => (
            <div
              className="p-4 border border-gray-400 shadow-md my-3"
              key={config.databaseConfigId.id}
            >
              <p>Configuration Name: {config.name}</p>
              <p> store name: {config.storeName}</p>
              
              {
                config.formDetails && config.formDetails.formStatus === "Completed" ? (
                  <div className="flex justify-end my-2 relative">
                  <button onClick={() => handleShowOptions(config)}><SlOptionsVertical /></button>
                  {
                    showOptions === config && (
                      <div className="flex flex-col gap-2 absolute top-5 right-0 bg-gray-100 px-8 py-4 rounded-lg">
                        <button className="btn btn-sm btn-accent text-white">Update</button>
                        <button onClick={() => setIsModalOpen(true)} className="btn btn-sm btn-error text-white">Delete</button>
                      </div>
                    )
                  }
                </div>
                ) : (
                  <Link
                className="btn btn-error btn-sm text-white my-4"
                to={`/input-configuration/${config.databaseConfigId.id}`}
              >
                Submit inputs
              </Link>
                )
              }
            </div>
          ))}
          <ConfirmationModal 
          open={isModalOpen} 
          onClose={() => setIsModalOpen(false)} 
          data={selectedConfig}
          action= {handleDeleteConfig}
          actionText={"Are you sure you want to delete this"}
          />
        </div>
      </div>
    </div>
  );
};

export default DBConfiguration;
