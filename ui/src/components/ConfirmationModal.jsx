import React from "react";

const ConfirmationModal = ({ open, onClose, data, action, actionText }) => {


  return (
    <div
      onClick={onClose}
      className={`
        fixed inset-0 flex justify-center items-center transition-colors
        ${open ? "visible bg-black/20" : "invisible"} z-50 
      `}
    >
      {/* modal */}
      <div
        onClick={(e) => e.stopPropagation()}
        className={`
          bg-white rounded border border-gray-300 shadow-lg px-4 py-10 transition-all 
          ${open ? "scale-100 opacity-100" : "scale-125 opacity-0"}
        `}
        style={{maxHeight: "80vh", overflow: "auto"}}
      >
        <div className="max-w-[500px] px-3 py-4">
          <p className="text-[18px] text-center"> {actionText} <span className="text-primary font-bold">{data?.name}?</span></p>
          <div className="mt-8 flex justify-center gap-x-20">
            <button
              onClick={() => action(data.databaseConfigId.id)}
              className="btn btn-error px-5 text-white"
            >
              Yes
            </button>
            <button
              onClick={onClose}
              className="btn btn-accent px-5 text-white"
            >
              No
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ConfirmationModal;
