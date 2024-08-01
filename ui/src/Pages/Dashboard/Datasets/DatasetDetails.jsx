// DatasetDetails.js
import { useEffect, useState } from "react";
import { FaArrowLeft } from "react-icons/fa6";
import { useNavigate, useParams } from "react-router-dom";
import DatasetDetailsTable from "../../../components/DatasetDetails/DatasetDetailsTable";
import { motion, AnimatePresence } from 'framer-motion';
import axios from "axios";
import Loading from "../../../components/Common/Loading";
import DatasetDetailsTabs from "../../../components/DatasetDetails/DatasetDetailsTabs";

const datasetData = [
  {
    tableName: "Students",
    columns: [
      {
        columnName: "studentName",
        columnType: {
          type: "NameType",
          isUnique: true,
        },
      },
      {
        columnName: "studentAddress",
        columnType: {
          type: "AddressType",
          isUnique: true,
        },
      },
    ],
    data: [
      {
        studentName: "Marty Labadie",
        studentAddress: "110 Gordon Causeway, Ryanport, OR 23397-9512",
      },
      {
        studentName: "Yelena Corkery",
        studentAddress: "464 O'Reilly Route, Michelbury, CO 88869",
      },
      {
        studentName: "Myron Trantow",
        studentAddress:
          "Apt. 069 841 Zoraida Trafficway, South Cristineborough, LA 93713-1572",
      },
      {
        studentName: "Santo Dibbert DDS",
        studentAddress: "Apt. 718 916 Manuel Junction, Miguelberg, VT 88690",
      },
      {
        studentName: "Guillermo Runte",
        studentAddress:
          "Suite 071 4371 Genaro Springs, Brainfurt, DE 05636-6606",
      },
    ],
  },
  {
    tableName: "Courses",
    columns: [
      {
        columnName: "courseName",
        columnType: {
          type: "StringType",
          isUnique: true,
        },
      },
      {
        columnName: "instructor",
        columnType: {
          type: "NameType",
          isUnique: false,
        },
      },
    ],
    data: [
      {
        courseName: "Introduction to Computer Science",
        instructor: "Dr. Alice Johnson",
      },
      {
        courseName: "Advanced Mathematics",
        instructor: "Prof. Bob Smith",
      },
      {
        courseName: "World History",
        instructor: "Dr. Carol Brown",
      },
      {
        courseName: "Environmental Science",
        instructor: "Prof. David Lee",
      },
      {
        courseName: "English Literature",
        instructor: "Dr. Emma Wilson",
      },
    ],
  },
];

const DatasetDetails = () => {
  const navigate = useNavigate();
  // const [datasetData, setDatasetData] = useState(null);
  // const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState(0);
  const [editableTableName, setEditableTableName] = useState(null);

  const { id } = useParams();

  const handleDoubleClick = (index) => {
    setEditableTableName(index);
  };

  const handleNameChange = (event, index) => {
    if (event.key === "Enter") {
      const newDatasetData = [...datasetData];
      newDatasetData[index].tableName = event.target.value;
      setEditableTableName(null);
      // here we will call the put request for update the table naem
    }
  };
  // useEffect(() => {
  //   setLoading(true);
  //   const fetchDataset = async () => {
  //     try {
  //       const res = await axios.get(
  //         `${import.meta.env.VITE_BASE_URL}/dataset/${id}/data`,
  //         {
  //           withCredentials: true,
  //         }
  //       );
  //       if (res.status === 200) {
  //         // Add a distinguishing field to each dataset
  //         const modifiedDatasets = res.data.datasets.map((dataset, index) => ({
  //           ...dataset,
  //           tableName: `Table ${index + 1}`,
  //           data: dataset.data.map(item => ({...item, tableIndex: index + 1}))
  //         }));
  //         setDatasetData(modifiedDatasets);
  //       }
  //     } catch (error) {
  //       console.error("Failed to fetch dataset:", error);
  //     } finally {
  //       setLoading(false);
  //     }
  //   };
  //   fetchDataset();
  // }, [id]);

  useEffect(() => {
    if (datasetData) {
      console.log("Active dataset:", datasetData[activeTab]);
    }
  }, [activeTab]);

  const tabsProps = {
    datasetData,
    activeTab,
    setActiveTab,
    handleDoubleClick,
    editableTableName,
    handleNameChange,
    setEditableTableName,
  }

  // if (loading) return <Loading />;

  return (
    <div className="py-8">
      <div className="ps-7 mb-5 flex items-center gap-3 -tracking-tighter">
        <FaArrowLeft
          className="cursor-pointer"
          onClick={() => navigate("/")}
          size={20}
        />
        <h2 className="text-[#8e8e8e] text-xl font-semibold">Dataset /</h2>
        <h2 className="text-xl font-semibold">{id}</h2>
      </div>
      <div className="w-[95%] h-[1px] bg-accent my-6"></div>

      <div className="ps-7 pe-9">
      {datasetData && (
        <div>
          <DatasetDetailsTabs props={tabsProps}/>
          <AnimatePresence mode="wait">
            <motion.div
              key={activeTab}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              transition={{ duration: 0.3 }}
            >
              <DatasetDetailsTable data={datasetData[activeTab]} />
            </motion.div>
          </AnimatePresence>
        </div>
      )}
    </div>
    </div>
  );
};

export default DatasetDetails;
