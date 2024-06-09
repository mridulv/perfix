import React from "react";

const AddButton = ({ setOpen, value }) => {
  return (
    <button
      onClick={() => setOpen(true)}
      className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#57B1FF]"
    >
      Add Database
    </button>
  );
};

export default AddButton;
