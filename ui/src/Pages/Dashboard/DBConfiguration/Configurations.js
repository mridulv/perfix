import axios from "axios";
import React, { useEffect } from "react";
import toast from "react-hot-toast";
import { useQuery } from "react-query";
import { useNavigate, useParams } from "react-router-dom";

const Configurations = () => {
  const { id } = useParams();
  
  const navigate = useNavigate();
  
  
 
  const {data: inputs, isLoading, refetch} = useQuery({
    queryKey: ["inputs", id],
    queryFn: async () => {
      const res = await axios.get(`http://localhost:9000/config/${id}/form`);
      const data = await res.data;
      return data;
    },
  });
;

  const handleSubmitInputs = async (event) => {
    event.preventDefault();

    const formData = new FormData(event.target);
    let values = [];
    formData.forEach((value, inputName) => {
      values.push({ inputName, answer: value });
    });

    
    const res = await axios.post(`http://localhost:9000/config/${id}/form`, {values}, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    
    if (res.status === 200) {
      await refetch();
    }
   
  };

  
  useEffect(() => {
    if(inputs === null){
      toast.success("Form Datas submitted successfully");
      navigate("/db-configuration");
    }
  }, [inputs, navigate])
  

  if(isLoading) return <p>loading..</p>
  return (
    <div className="flex flex-col items-center">
      <h3 className="text-xl font-bold text-center my-4">Please submit all the input fields to create the configuration completely</h3>
      <form className="max-w-[400px] mx-auto px-8 py-2 border border-gray-200 rounded shadow-md" onSubmit={handleSubmitInputs}>
        {inputs &&
          Object.entries(inputs.inputs).map(
            ([fieldName, { dataType, isRequired }], index) => (
              <div className="flex flex-col my-4" key={fieldName}>
                <label className="my-2">
                  {index + 1}. {fieldName}
                </label>
                {dataType === "StringType" && (
                  <input
                    type="text"
                    className="input input-bordered w-full max-w-xs"
                    style={{ outline: "none" }}
                    required={isRequired}
                    name={fieldName}
                    // {...register(`${fieldName}`, {required: isRequired})}
                  />
                )}
                {dataType === "IntType" && (
                  <input
                    type="number"
                    className="input input-bordered w-full max-w-xs"
                    style={{ outline: "none" }}
                    required={isRequired}
                    pattern="[0-9]*"
                    inputMode="numeric"
                    name={fieldName}
                    // {...register(`${fieldName}`, {required: isRequired})}
                  />
                )}
                {dataType === "BooleanType" && (
                  <div className="flex items-center">
                    <input
                      type="radio"
                      id={`${fieldName}-yes`}
                      name={fieldName}
                      value="true"
                      className="radio"
                      required={isRequired}
                      // {...register(`${fieldName}`, {required: isRequired})}
                    />
                    <label htmlFor={`${fieldName}-yes`} className="ml-2">
                      Yes
                    </label>
                    <input
                      type="radio"
                      id={`${fieldName}-no`}
                      name={fieldName}
                      value="false"
                      className="radio ml-4"
                      required={isRequired}
                      // {...register(`${fieldName}`, {required: isRequired})}
                    />
                    <label htmlFor={`${fieldName}-no`} className="ml-2">
                      No
                    </label>
                  </div>
                )}
              </div>
            )
          )}
          <input className="btn btn-md btn-primary text-white " type="submit" value={"Submit"}/>
      </form>
    </div>
  );
};

export default Configurations;
