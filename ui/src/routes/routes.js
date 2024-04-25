
import DashboardLayout from "../Layout/DashboardLayout"
import AddDBConfiguration from "../Pages/Dashboard/AddDBConfiguration/AddDBConfiguration";
import Configurations from "../Pages/Dashboard/DBConfiguration/Configurations";
import Dashboard from "../Pages/Dashboard/Dashboard/Dashboard";
import DBConfiguration from "../Pages/Dashboard/DBConfiguration/DBConfiguration"
import DatasetDetails from "../Pages/Dashboard/Datasets/DatasetDetails";
import Datasets from "../Pages/Dashboard/Datasets/Datasets";
import Experiment from "../Pages/Dashboard/Experiment/Experiment";


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
                path: "/input-configuration/:id",
                element: <Configurations/>
            },
            {
                path: "/datasets",
                element: <Datasets/>
            },
            {
                path: "/datasets/:id",
                element: <DatasetDetails/>,
            },
            {
                path: "/experiment",
                element: <Experiment/>
            },
        ]
    }
])