import axios from 'axios';
import React, { createContext, useEffect, useState } from 'react';

export const AuthContext = createContext();

const AuthProvider = ({children}) => {
    const [user, setUser] = useState(null);
    const [userLoading, setUserLoading] = useState(false)

    useEffect(() => {
        setUserLoading(true)
        const getUser = async() => {
            const res = await axios.get(`http://localhost:9001/me`);
            const userData = await res.data;
            setUser(userData);
            setUserLoading(false);
        };
        getUser();
    }, []);

    console.log(user);

    const values = {
        user,
        setUser,
        userLoading
    }
    return (
        <AuthContext.Provider value={values}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthProvider;