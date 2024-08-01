import { IoIosArrowForward } from "react-icons/io";

const StepHeader = ({creatingStep}) => {
  return (
    <div className="w-full bg-secondary flex items-center gap-2 md:gap-10">
        <div className="ps-7 py-3 ">
          <div
            className={`h-[45px] w-12 md:w-[220px] ps-3  ${
              creatingStep === "category" && "bg-white"
            }  flex items-center gap-3 rounded-xl`}
          >
            <div className="h-6 w-6 rounded-full bg-black text-white flex justify-center items-center">
              <p className="text-[14px]">1</p>
            </div>
            <p className="hidden md:block font-bold text-[12px]">Select Database Category</p>
          </div>
        </div>
        <IoIosArrowForward size={20} />
        <div className="ps-7 py-3 ">
          <div
            className={`h-[45px] w-12 md:w-[180px] ps-3  ${
              creatingStep === "dataset" && "bg-white"
            }  flex items-center gap-3 rounded-xl`}
          >
            <div className="h-6 w-6 rounded-full bg-black text-white flex justify-center items-center">
              <p className="text-[14px]">2</p>
            </div>
            <p className="hidden md:block font-bold text-[12px]">Setup Datasets</p>
          </div>
        </div>
        <IoIosArrowForward size={20} />
        <div
          className={`h-[45px] w-12 md:w-[180px] ps-3 ${
            creatingStep === "database" && "bg-white"
          } flex items-center gap-3 rounded-xl`}
        >
          <div className="h-6 w-6 rounded-full bg-black text-white flex justify-center items-center">
            <p className="text-[14px]">3</p>
          </div>
          <p className="hidden md:block font-bold text-[12px]">Setup Experiments</p>
        </div>
      </div>
  );
};

export default StepHeader;