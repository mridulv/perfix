import Dashboard from "../Pages/Dashboard/Dashboard/Dashboard";
import Datasets from "../Pages/Dashboard/Datasets/Datasets";
import Experiment from "../Pages/Dashboard/Experiment/Experiment";

const { createBrowserRouter } = require("react-router-dom");
const { default: DashboardLayout } = require("../Layout/DashboardLayout");
const { default: DBConfiguration } = require("../Pages/Dashboard/DBConfiguration/DBConfiguration");


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
                path: "/datasets",
                element: <Datasets/>
            },
            {
                path: "/experiment",
                element: <Experiment/>
            },
        ]
    }
])