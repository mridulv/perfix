import axiosApi from "./axios"

const fetchRelevantDatasets = async(category, setRelevantDatasets) => {
    try{
        const res = await axiosApi.get(`/experiment/config/${category}`);
        const data = await res.data;
        if(res.status === 200){
            setRelevantDatasets(data)
        }
    }catch(err){
        console.log(err);
    }
}

export default fetchRelevantDatasets;