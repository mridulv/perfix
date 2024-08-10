/* eslint-disable react-refresh/only-export-components */
import  { Suspense, lazy } from 'react';
import ProtectedRoute from "./ProtectedRoute";
import DashboardLayout from "../Layout/DashboardLayout/DashboardLayout";
import { createBrowserRouter, Navigate } from 'react-router-dom';
import Loading from '../components/Common/Loading';

// Lazy-loaded components
const ChatPage = lazy(() => import("../Pages/Dashboard/UseCases/ChatPage"));
const UseCases = lazy(() => import("../Pages/Dashboard/UseCases/UseCases"));
const DBConfiguration = lazy(() => import("../Pages/Dashboard/DBConfiguration/DBConfiguration"));
const DatasetDetails = lazy(() => import("../Pages/Dashboard/Datasets/DatasetDetails"));
const Datasets = lazy(() => import("../Pages/Dashboard/Datasets/Datasets"));
const AddExperimentPage = lazy(() => import("../Pages/Dashboard/Experiment/AddExperimentPage"));
const Experiment = lazy(() => import("../Pages/Dashboard/Experiment/Experiment"));
const ExperimentResultPage = lazy(() => import("../Pages/Dashboard/Experiment/ExperimentResultPage"));

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
        element: (
          <Suspense fallback={<Loading/>}>
            <Datasets />
          </Suspense>
        ),
      },
      {
        path: "/database",
        element: (
          <Suspense fallback={<Loading/>}>
            <DBConfiguration />
          </Suspense>
        ),
      },
      {
        path: "/experiment",
        element: (
          <Suspense fallback={<Loading/>}>
            <Experiment />
          </Suspense>
        ),
      },
      {
        path: "/add-experiment",
        element: (
          <Suspense fallback={<Loading/>}>
            <AddExperimentPage />
          </Suspense>
        ),
      },
      {
        path: "/experiment/:id",
        element: (
          <Suspense fallback={<Loading/>}>
            <ExperimentResultPage />
          </Suspense>
        ),
      },
      {
        path: "/dataset/:id",
        element: (
          <Suspense fallback={<Loading/>}>
            <DatasetDetails />
          </Suspense>
        ),
      },
      {
        path: "/usecases",
        element: (
          <Suspense fallback={<Loading/>}>
            <UseCases />
          </Suspense>
        ),
      },
      {
        path: "/usecases/:id",
        element: (
          <Suspense fallback={<Loading/>}>
            <ChatPage />
          </Suspense>
        ),
      },
    ],
  },
]);
