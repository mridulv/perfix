import { datasets } from "./data";

const { http, HttpResponse } = require("msw");

export const handlers = [
  http.get("${process.env.REACT_APP_BASE_URL}/dataset", () => {
    return HttpResponse.json(datasets);
  }),
  http.get("${process.env.REACT_APP_BASE_URL}/dataset/:id", ({ params }) => {
    const id = parseInt(params.id);
    const product = datasets.find((p) => p.id.id === id);

    if (!product) return new HttpResponse(null, { status: 404 });
    return HttpResponse.json(product);
  }),
  http.get("${process.env.REACT_APP_BASE_URL}/config", () => {
    return HttpResponse.json();
  }),
];
