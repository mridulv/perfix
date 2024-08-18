/* eslint-disable no-unused-vars */
import React from "react";
import { LuCircleEllipsis } from "react-icons/lu";
import { FaRegTimesCircle, FaRegPauseCircle, FaRegCheckCircle } from "react-icons/fa";
import { stateConstants } from "../../utils/stateConstants";

const StateIconComponent = ({state}) => {
  return (
      <div className="">
        {(() => {
          switch (state) {
            case stateConstants.Created:
              return <LuCircleEllipsis size={22} color="#1160aa" fontWeight={700}/>;
            case stateConstants.InProgress:
              return <FaRegPauseCircle size={21} color="#f49e04"/>;
            case stateConstants.Failed:
              return <FaRegTimesCircle size={21} color="#f54946"/>;
            case stateConstants.Completed:
              return  <FaRegCheckCircle  size={21} color="#91b951"/>;
            default:
              return null;
          }
        })()}
      </div>
  );
};

export default StateIconComponent;