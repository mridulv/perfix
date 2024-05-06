import React from "react";
import { Link } from "react-router-dom";

const DatasetCard = ({ dataset }) => {
  
  
  return (
    <div className="card  bg-purple-400 shadow-xl">
      <div className="card-body">
        <h2 className="text-white card-title">name: {dataset.name}</h2>
        
        <div className="flex justify-end  mt-3">
          <Link to={`/datasets/${dataset.id.id}`} className="btn btn-accent btn-sm text-white ">See Details</Link>
          <Link className="btn btn-success btn-sm  text-white ms-4" to={`/update-dataset/${dataset.id.id}`}>Update</Link>
        </div>
      </div>
    </div>
  );
};

export default DatasetCard;
