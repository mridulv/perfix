import axios from "axios";
import { createContext, useEffect, useState } from "react";

export const AuthContext = createContext();

const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [userLoading, setUserLoading] = useState(true);

  useEffect(() => {
    setUserLoading(true);
    const getUser = async () => {
      const res = await axios.get(`${import.meta.env.VITE_BASE_URL}/me`, {
        withCredentials: true,
      });
      const userData = await res.data;
      if (res.status === 200) {
        
        setUser(userData);
        setUserLoading(false);
      }
    };
    getUser();
  }, []);

  const values = {
    user,
    setUser,
    userLoading,
  };
  return <AuthContext.Provider value={values}>{children}</AuthContext.Provider>;
};

export default AuthProvider;
