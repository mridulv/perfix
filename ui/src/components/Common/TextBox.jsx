/* eslint-disable no-unused-vars */
import React, { useEffect, useRef } from 'react';
import Editor from "@monaco-editor/react";

const TextBox = ({ texts, onChange }) => {
  const editorRef = useRef(null);

  useEffect(() => {
    if (editorRef.current) {
      editorRef.current.setValue(texts);
    }
  }, [texts]);

  function handleEditorDidMount(editor) {
    editorRef.current = editor;
  }

  return (
    <div className="w-full md:w-[80%] lg:w-[85%] border-b-2 border-gray-300">
      <Editor
        height="200px"
        language="sql"
        value={texts}
        onChange={onChange}
        onMount={handleEditorDidMount}
        options={{
          selectOnLineNumbers: true,
          lineNumbers: 'on',
          roundedSelection: false,
          scrollBeyondLastLine: false,
          readOnly: false,
          minimap: { enabled: false }
        }}
      />
    </div>
  );
};

export default TextBox;