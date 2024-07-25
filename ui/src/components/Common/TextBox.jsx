import MonacoEditor from "react-monaco-editor";
import * as monaco from 'monaco-editor';

// Define custom theme
monaco.editor.defineTheme('myCustomTheme', {
  base: 'vs',
  inherit: true,
  rules: [],
  colors: {
    'editor.foreground': '#000000',
    'editor.background': '#FFFFFF',
    'editorLineNumber.foreground': '#000000',
    'editorLineNumber.background': '#E8E8E8',
    'editor.lineHighlightBackground': '#F0F0F0',
    'editorCursor.foreground': '#000000',
  }
});

const TextBox = ({ texts, onChange }) => {
  return (
    <div className="w-full md:w-[80%] lg:w-[85%] border-b-2 border-gray-300">
      <MonacoEditor
        width="100%"
        height="200"
        theme="myCustomTheme"
        language="sql"
        value={texts}
        onChange={onChange}
        options={{
          selectOnLineNumbers: true,
          lineNumbers: 'on',
          roundedSelection: false,
          scrollBeyondLastLine: false,
          readOnly: false,
          minimap: {
            enabled: false
          }
        }}
      />
    </div>
  );
};

export default TextBox;