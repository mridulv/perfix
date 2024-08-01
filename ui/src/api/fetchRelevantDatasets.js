import axiosApi from "./axios"

const fetchRelevantDatasets = async(category, setRelevantDatasets) => {
    try{
        const res = await axiosApi.get(`/experiment/dataset/${category}`);
        const data = await res.data;
        console.log(data);
        if(res.status === 200){
            setRelevantDatasets(data)
        }
    }catch(err){
        console.log(err);
    }
}

export default fetchRelevantDatasets;