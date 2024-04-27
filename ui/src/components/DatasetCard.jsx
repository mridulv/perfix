import React from "react";
import { Link } from "react-router-dom";

const DatasetCard = ({ dataset }) => {
  
  return (
    <div className="card w-60 bg-purple-400 shadow-xl">
      <div className="card-body">
        <h2 className="text-white card-title">{dataset.name}</h2>
        
        <div className="card-actions justify-end mt-3">
          <Link to={`/datasets/${dataset.id.id}`} className="btn btn-accent btn-sm text-white">See Details</Link>
        </div>
      </div>
    </div>
  );
};

export default DatasetCard;
