import Dashboard from "../Pages/Dashboard/Dashboard/Dashboard";
import DatasetDetails from "../Pages/Dashboard/Datasets/DatasetDetails";
import Datasets from "../Pages/Dashboard/Datasets/Datasets";
import Experiment from "../Pages/Dashboard/Experiment/Experiment";
import Home from "../Pages/Home/Home/Home";

const { createBrowserRouter } = require("react-router-dom");
const { default: DashboardLayout } = require("../Layout/DashboardLayout");
const { default: DBConfiguration } = require("../Pages/Dashboard/DBConfiguration/DBConfiguration");


export const router = createBrowserRouter([
    {
        path: "/home",
        element: <Home/>
    },
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