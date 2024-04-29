import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { useQuery } from 'react-query';
import { useNavigate, useParams } from 'react-router-dom';
import Loading from '../../../components/Loading';
import toast from 'react-hot-toast';

const UpdateDataset = () => {
    const { id } = useParams();
    const [columns, setColumns] = useState([]);
    const navigate = useNavigate();


    const {
      data: datasets,
      isLoading: datasetsLoading,
    } = useQuery({
      queryKey: ["datasets"],
      queryFn: async () => {
        const res = await axios.get("http://localhost:9000/dataset");
        const data = await res.data;
        console.log(data);
        return data;
      },
    });

    const {data: dataset, isLoading} = useQuery({
        queryKey: ["dataset", id],
        queryFn: async () => {
          const res = await axios.get(`http://localhost:9000/dataset/${id}`);
          const data = await res.data;
          return data.params;
        }
      });
      
    console.log(dataset);

    useEffect(() => {
      if (dataset) {
        const updatedColumns = dataset.columns.map((column) => ({
          columnName: column.columnName,
          columnType: column.columnType.type,
        }));
        setColumns(updatedColumns);
      }
    }, [dataset]);
  
    const handleAddColumn = () => {
      setColumns([...columns, { columnName: "", columnType: "" }]);
    };


    const handleUpdateDataset = async(event) => {
      event.preventDefault();
      const datasetName = event.target.datasetName.value;

      if (datasetName !== dataset.name) {
        const isDuplicateName = datasets.some(
          (existingDataset) => existingDataset.name.toLowerCase() === datasetName.toLowerCase()
        );
    
        if (isDuplicateName) {
          toast.error(`A dataset with the name "${datasetName}" already exists.`);
          return;
        }
      }

      const columnValues = columns.map((column, index) => {
        const columnName = event.target[`columnName${index}`].value;
        const columnType = event.target[`columnType${index}`].value;
        return { columnName, columnType };
      });
      console.log(datasetName, columnValues);
      try {
        const url = `http://localhost:9000/dataset/${id}`;
        const columnData = {
          rows: 10000,
          id: {
            id: dataset.id
          },
          name: datasetName,
          columns: columnValues.map((columnValue) => ({
            columnName: columnValue.columnName,
            columnType: {
              type: columnValue.columnType,
              isUnique: true,
            },
          })),
        };
    
        const response = await axios.post(url, columnData, {
          headers: {
            "Content-Type": "application/json",
          },
        });
    
        console.log(response.data);
        if (response.status === 200) {
          toast.success("Dataset updated successfully");
          navigate("/datasets")
        }
      } catch (err) {
        console.log(err);
      }
    }
     if (isLoading) {
        return <Loading/>;
    }
    return (
        <div className='flex  justify-center items-center'>
            <div>
            <h3 className='text-2xl font-bold my-5'>Update Dataset <span className='text-primary'>{dataset.name}</span></h3>
            <form onSubmit={handleUpdateDataset} className='w-[350px] md:w-[500px] px-7 py-4 border border-gray-100 shadow-lg'>
              <label className="form-control w-full max-w-xs mb-4">
                <div className="label">
                  <span className="label-text text-base">Name of the dataset.</span>
                </div>
                <input
                      className="border-2 border-gray-500 px-2 py-2 rounded mt-2"
                      placeholder="Dataset Name"
                      type="text"
                      name="datasetName"
                      required
                      defaultValue={dataset.name}
                    />
              </label>
              {columns.map((column, i) => (
            <div key={i}>
              <p className="text-sm">{i + 1}.</p>
              <div className="flex flex-col mb-4">
                <label htmlFor="">Column Name: </label>
                <input
                  className="border-2 border-gray-500 px-2 py-2 rounded mt-2 max-w-xs"
                  placeholder="Name"
                  type="text"
                  name={`columnName${i}`}
                  id={`columnName${i}`}
                  defaultValue={column.columnName}
                  required
                />
              </div>
              <div className="flex flex-col my-4">
                <label htmlFor="">Column Type:</label>
                <select
                  name={`columnType${i}`}
                  id={`columnType${i}`}
                  className="block w-full max-w-xs mt-2 px-4 py-2 pr-8 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-gray-500"
                >
                  <option value="NameType" selected={column.columnType === "NameType"}>
                    NameType
                  </option>
                  <option value="AddressType" selected={column.columnType === "AddressType"}>
                    AddressType
                  </option>
                </select>
              </div>
            </div>
          ))}
          <div className="flex justify-end mb-4">
            <button
              onClick={handleAddColumn}
              className="btn btn-xs btn-error text-white"
            >
              Add Column
            </button>
          </div>
          <div className="flex justify-center">
            <input
              type="submit"
              className="btn btn-primary text-center"
              value={"Update Dataset"}
            />
          </div>
            </form>
            </div>
        </div>
    );
};

export default UpdateDataset;