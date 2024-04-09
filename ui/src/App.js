import React, { useState, useEffect } from 'react';
import axios from 'axios';

function App() {
  const [selectedOption, setSelectedOption] = useState('');
  const [questionnaireId, setQuestionnaireId] = useState(null);
  const [formData, setFormData] = useState({});
  const [questionnaireComplete, setQuestionnaireComplete] = useState(false);
  const [experimentStarted, setExperimentStarted] = useState(false);

  const startQuestionnaire = async () => {
    try {
      const response = await axios.create({ baseURL: 'http://localhost:9000' }).post('/questionnaire?storeName=' + selectedOption);
      const newQuestionnaireId = response.data.id;
      console.log('New Questionnaire ID:', newQuestionnaireId);
      setQuestionnaireId(newQuestionnaireId);
    } catch (error) {
      console.error('Error:', error);
      // Handle error
    }
  };

  useEffect(() => {
    // This code will run when questionnaireId changes.
    if (questionnaireId !== null) {
      // Perform actions that depend on the updated questionnaireId here.
      // You can call the fetchQuestionFields function here if needed.
      console.log('Fetching question fields for ID:', questionnaireId);
      fetchQuestionFields();
    }
  }, [questionnaireId]); // This specifies that useEffect should run when questionnaireId changes.

  const fetchQuestionFields = () => {
    axios.create({baseURL: 'http://localhost:9000'})
      .get(`/questionnaire/${questionnaireId}/question`)
      .then((response) => {
        if (response.data === null) {
          setQuestionnaireComplete(true);
        } else {
          console.log(response.data);
          const fields = response.data.questions;
          setFormData(fields);
        }
      })
      .catch((error) => {
        // Handle error
      });
  };

  const submitAnswer = () => {
    axios.create({baseURL: 'http://localhost:9000'})
      .post(`/questionnaire/${questionnaireId}/question`, formData)
      .then(() => {
        fetchQuestionFields();
      }).then(() => {
        console.log(formData);
      })
      .catch((error) => {
        // Handle error
      });
  };

  const startExperiment = () => {
    axios.create({baseURL: 'http://localhost:9000'})
      .post(`/questionnaire/${questionnaireId}`)
      .then(() => {
        setExperimentStarted(true);
      })
      .catch((error) => {
        // Handle error
      });
  };

  return (
    <div className="App">
      <h1>Questionnaire and Experiment</h1>
      {!questionnaireId && (
        <div>
          <h2>Select an option:</h2>
          <select value={selectedOption} onChange={(e) => setSelectedOption(e.target.value)}>
            <option value="redis">Redis</option>
            <option value="mysql">MySQL</option>
            <option value="dynamodb">DynamoDB</option>
          </select>
          <button onClick={startQuestionnaire}>Start Questionnaire</button>
        </div>
      )}

      {questionnaireId && !questionnaireComplete && (
        <div>
          <h2>Question</h2>
          {Object.entries(formData).map(([field, fieldType]) => (
            <div key={field}>
              <label>{field}</label>
              <input
                type={fieldType}
                value={formData[field] || ''}
                onChange={(e) => setFormData({ ...formData, [field]: e.target.value })}
              />
            </div>
          ))}
          <button onClick={submitAnswer}>Submit</button>
        </div>
      )}

      {questionnaireComplete && !experimentStarted && (
        <div>
          <button onClick={startExperiment}>Start Experiment</button>
        </div>
      )}
    </div>
  );
}

export default App;