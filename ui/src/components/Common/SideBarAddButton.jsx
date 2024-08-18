/* eslint-disable no-unused-vars */
import React from "react";
import { useNavigate } from "react-router-dom";
import { FaPlus } from "react-icons/fa";
import axiosApi from "../../api/axios";
import toast from "react-hot-toast";

const SideBarAddButton = () => {
  const navigate = useNavigate();

  const handleNewConversation = async () => {
    const res = await axiosApi.post(`/usecases/create`, {});
    if (res.status === 200) {
      const id = res.data.useCaseId.id;
      navigate(`/usecases/${id}`);
    } else {
      toast.success("Couldn't create a conversation");
    }
  };

  return (
    <div>
      <button
        onClick={handleNewConversation}
        className="btn bg-[#A3D4FF] pe-8 h-[65px] rounded-2xl flex gap-4 hover:bg-[#57B1FF]"
      >
        <FaPlus color="#0A8DFF" size={20} />
        <span className="text-[16px] font-bold">UseCase</span>
      </button>
    </div>
  );
};

export default SideBarAddButton;
