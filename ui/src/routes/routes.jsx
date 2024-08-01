import { createBrowserRouter, Navigate } from "react-router-dom";
import ChatPage from "../Pages/Dashboard/UseCases/ChatPage";
import UseCases from "../Pages/Dashboard/UseCases/UseCases";
import DBConfiguration from "../Pages/Dashboard/DBConfiguration/DBConfiguration";
import DatasetDetails from "../Pages/Dashboard/Datasets/DatasetDetails";
import Datasets from "../Pages/Dashboard/Datasets/Datasets";
import AddExperimentPage from "../Pages/Dashboard/Experiment/AddExperimentPage";
import Experiment from "../Pages/Dashboard/Experiment/Experiment";
import ExperimentResultPage from "../Pages/Dashboard/Experiment/ExperimentResultPage";
import ProtectedRoute from "./ProtectedRoute";
import DashboardLayout from "../Layout/DashboardLayout/DashboardLayout";

export const router = createBrowserRouter([
  {
    path: "/",
    element: (
      <ProtectedRoute>
        <DashboardLayout />
      </ProtectedRoute>
    ),
    children: [
      {
        path: "/",
        element: <Navigate to="/dataset" replace />,
      },
      {
        path: "/dataset",
        element: <Datasets />,
      },
      {
        path: "/database",
        element: <DBConfiguration />,
      },
      {
        path: "/experiment",
        element: <Experiment />,
      },
      {
        path: "/add-experiment",
        element: <AddExperimentPage />,
      },
      {
        path: "/experiment-result/:id",
        element: <ExperimentResultPage />,
      },
      {
        path: "/dataset/:id",
        element: <DatasetDetails />,
      },
      {
        path: "/usecases",
        element: <UseCases />,
      },
      {
        path: "/usecases/:id",
        element: <ChatPage />,
      },
    ],
  },
]);
