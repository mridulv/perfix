/* eslint-disable no-unused-vars */
import React from 'react';
import Select from 'react-select';

const StyledReactSelect = ({ value, onChange, options }) => {
  const customStyles = {
    container: (provided) => ({
      ...provided,
      width: "250px",
      marginTop: "4px",
    }),
    control: (base) => ({
      ...base,
      fontSize: "13px",
    }),
    menu: (base) => ({
      ...base,
      fontSize: "13px",
    }),
  };

  return (
    <Select
      value={value}
      onChange={onChange}
      options={options}
      styles={customStyles}
    />
  );
};

export default StyledReactSelect;
