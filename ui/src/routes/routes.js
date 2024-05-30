import DashboardLayout from "../Layout/DashboardLayout";
import DBConfiguration from "../Pages/Dashboard/DBConfiguration/DBConfiguration";
import Datasets from "../Pages/Dashboard/Datasets/Datasets";
import Experiment from "../Pages/Dashboard/Experiment/Experiment";
import UpdateDataset from "../Pages/Dashboard/Datasets/UpdateDataset";
import AddExperiment from "../Pages/Dashboard/Experiment/AddExperiment";
import AddExperiment2 from "../Pages/Dashboard/Experiment/AddExperiment2";
import AddDBConfiguration from "../Pages/Dashboard/DBConfiguration/AddDBConfiguration";
import AddDBConfigurationDataset from "../Pages/Dashboard/DBConfiguration/AddDBConfigurationDataset";
import ProtectedRoute from "./ProtectedRoute";


const { createBrowserRouter } = require("react-router-dom");



export const router = createBrowserRouter([
    
    {
        path: "/",
        element: <ProtectedRoute><DashboardLayout/></ProtectedRoute>,
        children: [
            {
                path: "/",
                element: <Datasets/>
            },
            {
                path: "/database",
                element: <DBConfiguration/>
            },
            {
                path: "/add-database",
                element: <AddDBConfigurationDataset/>
            },
            {
                path: "/add-database/:datasetId",
                element: <AddDBConfiguration/>
            },
            
            {
                path: "/update-dataset/:id",
                element: <UpdateDataset/>
            },
            {
                path: "/experiment",
                element: <Experiment/>
            },
            {
                path: "/add-experiment-dataset",
                element: <AddExperiment/>
            },
            {
                path: "/add-experiment-database/:datasetId",
                element: <AddExperiment2/>
            },
        ]
    },
])