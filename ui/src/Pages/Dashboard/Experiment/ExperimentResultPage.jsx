/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { FaArrowLeft } from "react-icons/fa6";
import SingleDatabaseExperimentResult from "../../../components/ExperimentResults/SingleDatabaseExperimentResult";
import MultiDatabaseExperimentResult from "../../../components/ExperimentResults/MultiDatabaseExperimentResult";
import Loading from "../../../components/Common/Loading";
import fetchExperimentData from "../../../api/fetchExperimentData";


const ExperimentResultPage = () => {
  const { id } = useParams();
  const [experimentData, setExperimentData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [view, setView] = useState("table");
  const navigate = useNavigate();

  const handleView = (value) => {
    setView(value);
  };

  useEffect(() => {
    fetchExperimentData(id, setExperimentData, setIsLoading);
  }, [id]);

  if (isLoading) return <Loading />;
  return (
    <div className="py-8">
      <div className="ps-7 mb-5 flex items-center gap-3 -tracking-tighter">
        <FaArrowLeft
          className="cursor-pointer"
          onClick={() => navigate("/experiment")}
          size={20}
        />
        <h2 className="text-[#8e8e8e] text-xl font-semibold">Experiments /</h2>
        <h2 className="text-xl font-semibold">{experimentData.name}</h2>
      </div>
      <div className="w-[95%] h-[1px] bg-accent my-6"></div>

      {experimentData &&
        (experimentData.experimentResults.length > 1 ? (
          <div>
            <MultiDatabaseExperimentResult
              experimentData={experimentData}
              view={view}
              handleView={handleView}
            />
          </div>
        ) : (
          <div>
            <SingleDatabaseExperimentResult
              view={view}
              handleView={handleView}
              data={experimentData}
            />
          </div>
        ))}
    </div>
  );
};

export default ExperimentResultPage;
