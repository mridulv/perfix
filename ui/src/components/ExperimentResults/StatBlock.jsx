/* eslint-disable no-unused-vars */
import React from "react";

const StatBlock = ({ data, colors, selected }) => {
  return (
    <div>
      {data.map((item, index) => (
        <div className="p-5 border border-gray-300 rounded" key={index}>
          <p className="font-semibold mb-3">{item.name}</p>
          {item.values.map((value, valueIndex) => {
            const percentage = (value / item.maxValue) * 100;
            const showTimeUnit = item.name.toLowerCase().includes('time');
            const isVisible = selected[Object.keys(selected)[valueIndex]];
            return (
              <p key={valueIndex} className="flex items-center mb-2">
                <span
                  className={`h-1 inline-block mr-4 rounded transition-all duration-300 ${
                    isVisible ? 'opacity-100' : 'opacity-0'
                  }`}
                  style={{
                    width: `${percentage}%`,
                    backgroundColor: colors[valueIndex],
                  }}
                ></span>
                <span
                  className={`flex-shrink-0 transition-opacity duration-300 ${
                    isVisible ? 'opacity-100' : 'opacity-0'
                  }`}
                >
                  {isVisible ? `${value}${showTimeUnit ? 's' : ''}` : ''}
                </span>
              </p>
            );
          })}
        </div>
      ))}
    </div>
  );
};

export default StatBlock;
