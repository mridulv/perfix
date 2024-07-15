import { SlOptionsVertical } from "react-icons/sl";
import { Link, useNavigate } from "react-router-dom";
import StateIconComponent from "./StateIconComponent";


const CommonTableRows = ({
  data,
  columnHeads,
  showButtons,
  handleShowOptions,
  handleSelectedData,
}) => {
  const navigate = useNavigate();

  const handleRunExperiment = (id) => {
    navigate(`/experiment-result/${id}`);
  };

  return (
    <>
      {columnHeads[0] === "Dataset Name" && (
        <tr className="border-b border-gray-300 ps-2 hover:bg-accent ">
          <td
            data-tip="Click to see the details"
            onClick={() => navigate(`/dataset/${data.id.id}`)}
            className="w-full text-start text-[13px] py-4 ps-2 tooltip cursor-pointer hover:underline"
          >
            {data.name}
          </td>
          <td className="text-start text-[13px] py-4 ps-2">
            {data.columns.length}
          </td>
          <td className="text-start text-[13px] py-4 ps-2">
            {new Date(data.createdAt).toLocaleDateString()}
          </td>
          <td className="text-start text-[13px] py-4 ps-2">{data.rows}</td>
          <td className="relative w-[50px]">
            <button
              onClick={() => handleShowOptions(data)}
              className="btn-sm hover:bg-accent rounded"
            >
              <SlOptionsVertical size={13} />
            </button>
            {showButtons === data && (
              <div className="flex flex-col justify-center gap-1 absolute z-10 bottom-[-70px] left-[-15px] bg-white shadow-md rounded p-2 actions">
                <button className="px-2 py-1 text-[13px] hover:bg-accent">
                  Edit
                </button>
                <button
                  onClick={() => handleSelectedData(data)}
                  className="px-2 py-1 text-[13px] hover:bg-accent"
                >
                  Delete
                </button>
              </div>
            )}
          </td>
        </tr>
      )}
      {columnHeads[0] === "Database Name" && (
        <tr className="border-b border-gray-300 ps-2 hover:bg-accent">
          <td className="text-start text-[13px] py-4 ps-2">{data.name}</td>
          <td className="text-start text-[13px] py-4 ps-2">
            {data.databaseSetupParams.type}
          </td>
          <td className="text-start text-[13px] py-4 ps-2 hover:underline hover:text-primary">
            <Link to={`/dataset/${data.datasetDetails.datasetId.id}`}>
            {data.datasetDetails.datasetName}
            </Link>
          </td>
          <td className="text-start text-[13px] py-4 ps-2">
            {new Date(data.createdAt).toLocaleDateString()}
          </td>
          <td className="relative w-[50px]">
            <button
              onClick={() => handleShowOptions(data)}
              className="btn-sm hover:bg-accent rounded"
            >
              <SlOptionsVertical size={13} />
            </button>
            {showButtons === data && (
              <div className="flex flex-col justify-center gap-1 absolute z-10 bottom-[-70px] left-[-15px] bg-white shadow-md rounded p-2 actions">
                <button className="px-2 py-1 text-[13px] hover:bg-accent">
                  Edit
                </button>
                <button
                  onClick={() => handleSelectedData(data)}
                  className="px-2 py-1 text-[13px] hover:bg-accent"
                >
                  Delete
                </button>
              </div>
            )}
          </td>
        </tr>
      )}

      {columnHeads[0] === "Experiment Name" && (
        <tr className="border-b border-gray-300 ps-2 hover:bg-accent cursor-pointer">
          <td
            onClick={() => handleRunExperiment(data?.experimentId.id)}
            data-tip="Click to run the experiment"
            className="w-full text-start text-[13px] py-4 ps-2 tooltip"
          >
            {data.name}
          </td>
          <td className="text-start text-[13px] py-4 ps-2 relative text-primary">
            {data.databaseConfigs.length <= 2 ? (
              data.databaseConfigs.map((config, index) => (
                <span key={config.id || index}>
                  {config.databaseConfigName}
                  {index < data.databaseConfigs.length - 1 && ", "}
                </span>
              ))
            ) : (
              <>
                {data.databaseConfigs.slice(0, 2).map((config, index) => (
                  <span key={config.id || index}>
                    {config.databaseConfigName}
                    {index < 1 && ", "}
                  </span>
                ))}
                <span
                  className="text-black cursor-pointer ml-1 relative group underline"
                  data-tip={data.databaseConfigs
                    .slice(2)
                    .map((c) => c.databaseConfigName)
                    .join(", ")}
                >
                  {` +${data.databaseConfigs.length - 2} more`}
                  <span className="text-primary invisible group-hover:visible absolute left-0 top-full mt-1 bg-white border border-gray-200 rounded p-2 shadow-md z-10 whitespace-nowrap">
                    {data.databaseConfigs
                      .slice(2)
                      .map((c) => c.databaseConfigName)
                      .join(", ")}
                  </span>
                </span>
              </>
            )}
          </td>
          <td className="text-start text-[13px] py-4 ps-2">
            {new Date(data.createdAt).toLocaleDateString()}
          </td>
          <td data-tip={data.experimentState} className="tooltip">
            <StateIconComponent state={data.experimentState}/>
          </td>

          <td className="relative w-[50px]">
            <button
              onClick={() => handleShowOptions(data)}
              className="btn-sm hover:bg-accent rounded"
            >
              <SlOptionsVertical size={13} />
            </button>
            {showButtons === data && (
              <div className="flex flex-col justify-center gap-1 absolute z-10 bottom-[-70px] left-[-15px] bg-white shadow-md rounded p-2 actions">
                <button className="px-2 py-1 text-[13px] hover:bg-accent">
                  Edit
                </button>
                <button
                  onClick={() => handleSelectedData(data)}
                  className="px-2 py-1 text-[13px] hover:bg-accent"
                >
                  Delete
                </button>
              </div>
            )}
          </td>
        </tr>
      )}

      {columnHeads[0] === "Use Cases" && (
        <tr className="border-b border-gray-300 ps-2 hover:bg-accent">
          <td  
          onClick={() => navigate(`/usecases/${data.useCaseId.id}`)}
          className="text-start text-[13px] py-4 ps-2 cursor-pointer">{data.name}</td>
          <td className="text-start text-[13px] py-4 ps-2">
            {new Date(data.createdAt).toLocaleDateString()}
          </td>
          <td data-tip={data.useCaseState} className="tooltip py-4 ps-2">
            <StateIconComponent state={data.useCaseState}/>
          </td>
          <td className="relative w-[50px]">
            <button
              onClick={() => handleShowOptions(data)}
              className="btn-sm hover:bg-accent rounded"
            >
              <SlOptionsVertical size={13} />
            </button>
            {showButtons === data && (
              <div className="flex flex-col justify-center gap-1 absolute z-10 bottom-[-70px] left-[-15px] bg-white shadow-md rounded p-2 actions">
                <button className="px-2 py-1 text-[13px] hover:bg-accent">
                  Edit
                </button>
                <button
                  onClick={() => handleSelectedData(data)}
                  className="px-2 py-1 text-[13px] hover:bg-accent"
                >
                  Delete
                </button>
              </div>
            )}
          </td>
        </tr>
      )}
    </>
  );
};
export default CommonTableRows;
