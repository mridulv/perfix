import axios from "axios";
import React from "react";
import { useQuery } from "react-query";
import { useParams } from "react-router-dom";

const DatasetDetails = () => {
  const {id} = useParams();

  const {data: dataset, isLoading} = useQuery({
    queryKey: ["dataset", id],
    queryFn: async () => {
      const res = await axios.get(`http://localhost:9000/dataset/${id}`);
      const data = await res.data;
      return data;
    },
    
  })
  
    if (isLoading) {
      return <div>Loading...</div>;
    }
    const { params } = dataset;
    return (
    <div className="w-[90%] mx-auto mt-4 overflow-x-auto">
      <p>Dataset : {dataset.name}</p>
      <table className="table">
        {/* head */}
        <thead>
          <tr>
            <th></th>
            {
                params?.columns.map((column, i) => (
                    <th key={i}>{column.columnName}</th>
                ))
            }
          </tr>
        </thead>
        <tbody>
          {/* row 1 */}
          <tr>
            <th>1</th>
            
          </tr>
          {/* row 2 */}
          <tr>
            <th>2</th>
            
          </tr>
          {/* row 3 */}
          <tr>
            <th>3</th>
          </tr>
          <tr>
            <th>4</th>
          </tr>
          <tr>
            <th>5</th>
          </tr>
        </tbody>
      </table>
    </div>
  );
};

export default DatasetDetails;
