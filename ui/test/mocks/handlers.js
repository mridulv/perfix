import { datasets } from "./data";

const { http, HttpResponse } = require("msw");


export const handlers = [
  http.get("http://localhost:9000/dataset", () => {
    return HttpResponse.json(datasets);
  }),
  http.get("http://localhost:9000/dataset/:id", ({ params }) => {
    const id = parseInt(params.id);
    const product = datasets.find((p) => p.id.id === id);

    if (!product) return new HttpResponse(null, { status: 404 });
    return HttpResponse.json(product);
  }),
  http.get("http://localhost:9000/config", () => {
    return HttpResponse.json()
  })
];
