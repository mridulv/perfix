import DashboardLayout from "../Layout/DashboardLayout";
import DBConfiguration from "../Pages/Dashboard/DBConfiguration/DBConfiguration";
import Datasets from "../Pages/Dashboard/Datasets/Datasets";
import AddExperimentPage from "../Pages/Dashboard/Experiment/AddExperimentPage";
import Experiment from "../Pages/Dashboard/Experiment/Experiment";
import ExperimentResultPage from "../Pages/Dashboard/Experiment/ExperimentResultPage";
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
                path: "/add-experiment",
                element: <AddExperimentPage/>
            },
            {
                path: "/experiment-result/:id",
                element: <ExperimentResultPage/>
            },
        ]
    },
])