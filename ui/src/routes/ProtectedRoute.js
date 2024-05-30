import React, { useContext } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import Loading from '../components/Loading';
import { AuthContext } from '../contexts/AuthProvider';
import Authentication from '../Pages/Authentication/Authentication';

const ProtectedRoute = ({children}) => {
    const {user, userLoading} = useContext(AuthContext);
    const location = useLocation();

    if(userLoading){
        return <Loading/>
    }

    if(Object.keys(user).length > 0){
        return children;
    }
    return <Authentication/>;
    
    
};

export default ProtectedRoute;