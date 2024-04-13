import React, { useEffect, useState } from 'react';
import axios from "axios"

const Datasets = () => {

    const [datasets, setDatasets] = useState({});

    useEffect(() => {
        const fetchDatasets = async() => {
            const res = await axios.get("http://localhost:9000/dataset");
            const data = await res.data;
            console.log(data);
        }

        fetchDatasets();
    }, [])

    const handleSubmit = async(event) => {
        event.preventDefault();
        const form = event.target;
        const name = form.name.value;
        const number = form.number.value;
        const desc = form.description.value;
        console.log(name, number, desc);

        try{
            const url = "";
            const data = {};
            const response = await axios.post(url, data);
            console.log(response.data);
        }
        catch(err){
            console.log(err);
        }
    }
    return (
        <div className='h-full flex justify-center items-center'>
            <form  className='w-[300px] ' onSubmit={handleSubmit}>
                <div className='flex flex-col my-4'>
                    <label htmlFor="">Dataset Name: </label>
                    <input className='border-2 border-gray-500 px-2 py-2 rounded mt-2' placeholder='Name' type="text" name="name" id="" required/>
                </div>
                <div className='flex flex-col my-4'>
                    <label htmlFor="">Number of Columns: </label>
                    <input className='border-2 border-gray-500 px-2 py-2 rounded mt-2' placeholder='Number' type="number" name="number" id="" min={1} max={10} required/>
                </div>
                <div className='flex flex-col my-4'>
                    <label htmlFor="">Dataset Description: </label>
                    <textarea  className='border-2 border-gray-500 px-2 py-2 rounded mt-2 resize-none' name="description" id="" cols="30" rows="10" required></textarea>
                </div>
                <div className='flex justify-center'>
                    <button className='bg-purple-400 text-white px-4 py-2 rounded-md'>Submit</button>
                </div>
            </form>
        </div>
    );
};

export default Datasets;