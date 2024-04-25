import React from 'react';

const Loading = () => {
    return (
        <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "100vh",
        }}
      >
        <span className="loading loading-spinner loading-lg"></span>
      </div>
    );
};

export default Loading;