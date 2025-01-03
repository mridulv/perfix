import { RouterProvider } from 'react-router-dom';
import { router } from './routes/routes';
import { Toaster } from 'react-hot-toast';
import "./index.css";

function App() {
  

  return (
    <div>
      <Toaster/>
      <RouterProvider router={router}>

      </RouterProvider>
    </div>
  );
}

export default App;