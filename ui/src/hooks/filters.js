import { useEffect, useState } from "react";
import fetchDatabaseTypes from "../api/fetchDatabaseTypes";

export const Filters = () => {
    const [databaseTypes, setDatabaseTypes] = useState([]);
    const [selectedDatabaseType, setSelectedDatabaseType] = useState({
        option: "Choose Database",
        value: "choose",
      });
      const [selectedDatasetName, setSelectedDatasetName] = useState({
        option: "Choose Dataset",
        value: "choose",
      });
      const [selectExperimentState, setSelectExperimentState] = useState({
        option: "Choose State",
        value: "choose",
      });

    useEffect(() => {
        fetchDatabaseTypes(setDatabaseTypes);
    }, []);

    const databaseTypesOptions = databaseTypes?.map(type => ({
        option: type,
        value: type
    }));

    const experimentStates = ["Created", "InProgress", "Completed", "Failed"];
    const experimentStateOptions = experimentStates.map(state => ({option: state, value: state}));



    return {
        selectedDatabaseType,
        setSelectedDatabaseType,
        databaseTypesOptions,
        selectedDatasetName,
        setSelectedDatasetName,
        selectExperimentState,
        setSelectExperimentState,
        experimentStateOptions
    }
}


