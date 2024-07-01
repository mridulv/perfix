import axiosApi from "./axios";

const fetchInputFields = async (
  selectedDatabaseType,
  setInputFields,
  setMaxSteps,
) => {
  try {
    const res = await axiosApi.get(`/databases/inputs?databaseType=${selectedDatabaseType.value}`);
    if (res.status === 200) {
      setInputFields(res.data.forms);
      setMaxSteps(res.data.forms.length + 1);
    }
  } catch (error) {
    console.error("Error fetching input fields:", error);
    // Handle error state if needed
  }
};

export default fetchInputFields;
