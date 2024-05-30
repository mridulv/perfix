import React from "react";
import { MdClose } from "react-icons/md";

const DeleteModal = ({
  open,
  onClose,
  data,
  action,
  actionHead,
  actionText,
}) => {
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
          bg-white rounded border border-gray-300 shadow-lg pt-6 pb-4 transition-all 
          ${open ? "scale-100 opacity-100" : "scale-125 opacity-0"}
        `}
        style={{ maxHeight: "80vh", overflow: "auto" }}
      >
        <button
          onClick={onClose}
          className="absolute top-5 right-2 p-1 rounded-lg text-gray-400 bg-white hover:bg-gray-50 hover:text-gray-600"
        >
          <MdClose size={25} />
        </button>
        <div className="max-w-[568px]">
          <h2 className="px-6 text-xl font-semibold">{actionHead}</h2>
          <div className="w-[100%] h-[1px] bg-accent mt-6 mb-5"></div>

          <p className="px-6 text-sm">{actionText}</p>
          <div className="w-[100%] h-[1px] bg-accent mt-6 mb-5"></div>
          <div className="mt-8 me-6 flex justify-end gap-x-4">
            <button
              onClick={onClose}
              className="btn btn-sm bg-white  border border-gray-300 rounded"
            >
              Don't Delete
            </button>
            <button
              onClick={() => action(data.id.id)}
              className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#6b3b51d2]"
            >
              Delete
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DeleteModal;
