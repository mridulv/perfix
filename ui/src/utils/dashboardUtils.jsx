import axios from "axios";
import toast from "react-hot-toast";
import datasetIcon from "../assets/dataset.png";
import databaseIcon from "../assets/database.png";
import experimentIcon from "../assets/experiment.png";
import useCasesIcon from "../assets/conversationIcon.png";

export const menus = [
  {
    name: "Datasets",
    link: "/dataset",
    icon: datasetIcon,
  },
  {
    name: "Database Configuration",
    link: "/database",
    icon: databaseIcon,
  },
  {
    name: "Experiment",
    link: "/experiment",
    icon: experimentIcon,
  },
  {
    name: "Use Cases",
    link: "/usecases",
    icon: useCasesIcon
  },
];

export const handleLogout = async (setUser) => {
  try {
    const res = await axios.get(`${import.meta.env.VITE_BASE_URL}/logout`, {
      withCredentials: true,
    });
    console.log(res);
    if (res.status === 200) {
      toast.success("Logged out successfully!");
      setUser({});
    }
  } catch (error) {
    console.error("Logout failed:", error);
    toast.error("Logout failed. Please try again.");
  }
};
