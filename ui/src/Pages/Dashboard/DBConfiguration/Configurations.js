import axios from "axios";
import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useParams } from "react-router-dom";

const Configurations = () => {
  const { id } = useParams();
  const [formDatas, setFormDatas] = useState(null);
  const [loading, setLoading] = useState(false);


  const {register, handleSubmit} = useForm()
 
  useEffect(() => {
    setLoading(true)
    const fetchFormDatas = async () => {
      const res = await axios.get(`http://localhost:9000/config/${id}/inputs`);
      const data = await res.data;
      console.log(data);
      if(res.status === 200){
        setFormDatas(data.inputs);
        setLoading(false)
      }
    };

    fetchFormDatas();
  }, [id]);

  // const datas =  Object.entries(formDatas).map( => );
  // console.log(datas);

  const handleSubmitInputs = async (data) => {
    console.log(data);
    const values = Object.entries(data).map(([inputName, answer]) => ({
      inputName,
      answer: answer === null ? "" : answer,
    }));
    console.log(values);
    const res = await axios.post(`http://localhost:9000/config/${id}/submit`, {values}, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    const responseData = await res.data;
    console.log(responseData);
  };

  if(loading) return <p>loading..</p>

  return (
    <div className="flex flex-col items-center">

      <form onSubmit={handleSubmit(handleSubmitInputs)}>
        {formDatas &&
          Object.entries(formDatas).map(
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
                    {...register(`${fieldName}`, {required: isRequired})}
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
                    {...register(`${fieldName}`, {required: isRequired})}
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
                      {...register(`${fieldName}`, {required: isRequired})}
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
                      {...register(`${fieldName}`, {required: isRequired})}
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
