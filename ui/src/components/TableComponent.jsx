import React from "react";

const headers = ["Product name", "Color", "Category", "Price"];

const products = [
  { name: "Product 1", color: "black", category: "Category 1", price: "20" },
  { name: "Product 2", color: "black", category: "Category 1", price: "20" },
  { name: "Product 3", color: "black", category: "Category 1", price: "20" },
  { name: "Product 4", color: "black", category: "Category 1", price: "20" },
  { name: "Product 2", color: "black", category: "Category 1", price: "20" },
];

const TableComponent = () => {
  return (
    <div className="relative overflow-x-auto px-3" style={{backgroundColor: "#1f2937", height: "400px"}}>
      <h3 className="text-xl font-bold text-start text-gray-500 my-2">First Table</h3>
      <table className="w-full text-gray-500 dark:text-gray-400 border border-gray-700">
        <thead className=" text-xs text-gray-700 uppercase border-b border-gray-700 dark:text-white">
          <tr style={{padding: "10px 0"}}>
            {headers.slice(0, 3).map((header) => (
              <th
                style={{ fontSize: "9px" }}
                key={header}
                scope="col"
                className="text-gray-500 py-1 border-r border-gray-700 dark:border-whit"
              >
                {header}
              </th>
            ))}
            <th style={{ fontSize: "9px" }} className=" py-1">
              {headers[3]}
            </th>
          </tr>
        </thead>
        <tbody>
          {products.map((product) => (
            <tr className="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
              <th
                scope="row"
                className="px-6 py-4  text-gray-500 whitespace-nowrap "
                style={{fontSize: "11px"}}
              >
                {product.name}
              </th>
              <td className="px-6 py-4" style={{fontSize: "11px"}}>{product.color}</td>
              <td className="px-6 py-4" style={{fontSize: "11px"}}>{product.category}</td>
              <td className="px-6 py-4" style={{fontSize: "11px"}}>${product.price}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TableComponent;
