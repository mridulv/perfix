import DashboardLayout from "../Layout/DashboardLayout";
import DBConfiguration from "../Pages/Dashboard/DBConfiguration/DBConfiguration";
import Datasets from "../Pages/Dashboard/Datasets/Datasets";
import Experiment from "../Pages/Dashboard/Experiment/Experiment";
import AddExperiment from "../Pages/Dashboard/Experiment/AddExperiment";
import AddExperiment2 from "../Pages/Dashboard/Experiment/AddExperiment2";
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