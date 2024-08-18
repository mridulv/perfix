/* eslint-disable no-unused-vars */
import { useEffect, useState } from "react";
import { FaArrowLeft } from "react-icons/fa6";
import { useNavigate, useParams } from "react-router-dom";
import fetchExperimentData from "../../../api/fetchExperimentData";
import Loading from "../../../components/Common/Loading";
import ExperimentForm from "../../../components/Experiment/ExperimentForm";
import axiosApi from "../../../api/axios";

const UpdateExperimentPage = () => {
  const { id } = useParams();
  const [experimentData, setExperimentData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [selectedDatasetId, setSelectedDatasetId] = useState(null);
  const [selectedDatasetData, setSelectedDatasetData] = useState(null);
  const navigate = useNavigate();

  if (!id) {
    navigate("/experiment");
  }

  useEffect(() => {
    if(experimentData){
      const fetchDatabase = async () => {
        const res = await axiosApi.get(`/config/${
            experimentData?.databaseConfigs[0]?.databaseConfigId.id
          }`
        );
        const data = await res.data;
        const datasetId = data?.datasetDetails?.datasetId?.id;
        setSelectedDatasetId(datasetId);
      };
      fetchDatabase();
    }
  }, [experimentData])

  useEffect(() => {
    if (experimentData && selectedDatasetId) {
      const fetchDataset = async () => {
        const res = await axiosApi.get(
          `${import.meta.env.VITE_BASE_URL}/dataset/${selectedDatasetId}`
        );
        const data = await res.data;
        setSelectedDatasetData(data);
      };
      fetchDataset();
    }
  }, [experimentData, selectedDatasetId]);


  useEffect(() => {
  fetchExperimentData(id, setExperimentData, setLoading)
  }, [id])

  if(loading) return <Loading/>
  return (
    <div className="pt-8 flex flex-col min-h-screen">
      <div className="ps-1 md:ps-7 mb-5 flex items-center gap-3 md:gap-0 -tracking-tighter">
        <FaArrowLeft
          className="cursor-pointer"
          onClick={() => navigate("/experiment")}
          size={20}
        />
        <h2 className="ms-2 text-[#8e8e8e] text-xs  md:text-xl font-semibold">
          Update Experiment /
        </h2>
        <h2 className="ms-2 text-xs md:text-xl font-semibold">
          {experimentData?.name}
        </h2>
      </div>
      <div className="w-[95%] h-[1px] bg-accent my-6"></div>

      <div>
        {selectedDatasetData && selectedDatasetId && (
          <ExperimentForm
            dataset={selectedDatasetData}
            selectedDatabaseCategory={experimentData.databaseCategory}
            experimentData={experimentData}
            isUpdate={true}
          />
        )}
      </div>
    </div>
  );
};

export default UpdateExperimentPage;
