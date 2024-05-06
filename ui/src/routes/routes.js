
import DashboardLayout from "../Layout/DashboardLayout"
import AddDBConfiguration from "../Pages/Dashboard/AddDBConfiguration/AddDBConfiguration";
import Configurations from "../Pages/Dashboard/DBConfiguration/Configurations";
import Dashboard from "../Pages/Dashboard/Dashboard/Dashboard";
import DBConfiguration from "../Pages/Dashboard/DBConfiguration/DBConfiguration"
import DatasetDetails from "../Pages/Dashboard/Datasets/DatasetDetails";
import Datasets from "../Pages/Dashboard/Datasets/Datasets";
import Experiment from "../Pages/Dashboard/Experiment/Experiment";
import UpdateDataset from "../Pages/Dashboard/Datasets/UpdateDataset";
import UpdateDBConfiguration from "../Pages/Dashboard/UpdateDBConfiguration/UpdateDBConfiguration";
import UpdateConfiguration from "../Pages/Dashboard/DBConfiguration/UpdateConfiguration";
import AddExperiment from "../Pages/Dashboard/Experiment/AddExperiment";
import AddExperiment2 from "../Pages/Dashboard/Experiment/AddExperiment2";
import AddDatasetPage from "../Pages/Dashboard/Datasets/AddDatasetPage";


const { createBrowserRouter } = require("react-router-dom");



export const router = createBrowserRouter([
    
    {
        path: "/",
        element: <DashboardLayout/>,
        children: [
            {
                path: "/",
                element: <Dashboard/>
            },
            {
                path: "/db-configuration",
                element: <DBConfiguration/>
            },
            {
                path: "/add-db-configuration",
                element: <AddDBConfiguration/>
            },
            {
                path: "/update-db-configuration/:id",
                element: <UpdateDBConfiguration/>
            },
            {
                path: "/input-configuration/:id",
                element: <Configurations/>
            },
            {
                path: "/update-input-configuration/:id",
                element: <UpdateConfiguration/>
            },
            {
                path: "/datasets",
                element: <Datasets/>
            },
            {
                path: "/add-dataset",
                element: <AddDatasetPage/>
            },
            {
                path: "/datasets/:id",
                element: <DatasetDetails/>,
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
                path: "/add-experiment-database",
                element: <AddExperiment2/>
            },
        ]
    }
])