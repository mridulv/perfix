import React, { useState, useEffect } from 'react';
import axios from 'axios';

const ExperimentComponent = () => {
  const [experimentId, setExperimentId] = useState(null);
  const [questionFields, setQuestionFields] = useState([]);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [formData, setFormData] = useState({}); // User input data for the current question
  const [experimentStarted, setExperimentStarted] = useState(false);

  // Function to start the experiment
  const startExperiment = async () => {
    try {
      // Make an API call to start the experiment and get the experiment ID
      const response = await axios.post('/questionnaire');
      setExperimentId(response.data.id);
      setExperimentStarted(true);
      fetchQuestionFields(0); // Fetch the first question's input fields
    } catch (error) {
      console.error('Error starting the experiment', error);
    }
  };

  // Function to fetch input fields for a question
  const fetchQuestionFields = async (questionIndex) => {
    try {
      // Make an API call to get input fields for the current question
      const response = await axios.get(`/questionnaire/${experimentId}/question`);
      setQuestionFields(response.data.fields);
    } catch (error) {
      console.error('Error fetching question fields', error);
    }
  };

  // Function to submit user input for the current question
  const submitAnswer = async () => {
    try {
      // Make an API call to submit user input
      await axios.post(`/questionnaire/${experimentId}/question`, formData);
      setCurrentQuestionIndex(currentQuestionIndex + 1);
      setFormData({}); // Clear the form data
      fetchQuestionFields(currentQuestionIndex + 1); // Fetch input fields for the next question
    } catch (error) {
      console.error('Error submitting answer', error);
    }
  };

  // Render the experiment UI
  return (
    <div>
      {experimentStarted ? (
        <div>
          {questionFields.length > 0 ? (
            <div>
              <h2>Question {currentQuestionIndex + 1}</h2>
              <form>
                {questionFields.map((field) => (
                  <div key={field.name}>
                    <label>{field.label}</label>
                    <input
                      type={field.type}
                      name={field.name}
                      value={formData[field.name] || ''}
                      onChange={(e) =>
                        setFormData({ ...formData, [field.name]: e.target.value })
                      }
                    />
                  </div>
                ))}
                <button type="button" onClick={submitAnswer}>
                  Submit
                </button>
              </form>
            </div>
          ) : (
            <button onClick={() => startExperiment()}>Start Experiment</button>
          )}
        </div>
      ) : (
        <button onClick={() => startExperiment()}>Start Experiment</button>
      )}
    </div>
  );
};

export default ExperimentComponent;