import { useState } from "react";
import TextBox from "../Common/TextBox";
import CustomSelect from "../CustomSelect/CustomSelect";

const QueryComponentForRDBMS = ({ selectedOption }) => {
  const [texts, setTexts] = useState("select * from students");
  const [demoSelect, setDemoSelect] = useState({
    option: "Choose Column",
    value: "Choose",
  });

  const onChangeText = (newValue) => {
    setTexts(newValue);
  };
  return (
    <div>
      {selectedOption === "sequel" ? (
        <TextBox texts={texts} onChange={onChangeText} />
      ) : (
        <div className="flex items-center gap-3">
          <p className="text-sm">select</p>
          <CustomSelect
            selected={demoSelect}
            setSelected={setDemoSelect}
            options={[]}
            width="w-[160px]"
          />
          <p className="text-sm">where</p>
          <CustomSelect
            selected={demoSelect}
            setSelected={setDemoSelect}
            options={[]}
            width="w-[160px]"
          />
          <p>=</p>
          <input
            className="search-input"
            style={{ width: "160px" }}
            placeholder="Enter Value"
          />
        </div>
      )}
    </div>
  );
};

export default QueryComponentForRDBMS;
