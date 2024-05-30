import React, { useContext } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import Loading from '../components/Loading';
import { AuthContext } from '../contexts/AuthProvider';

const ProtectedRoute = ({children}) => {
    const {user, userLoading} = useContext(AuthContext);
    console.log(user);
    const location = useLocation();

    if(userLoading){
        return <Loading/>
    }
    if(user && Object.keys(user).length > 0){
        return children;
    }
    return <Navigate to="/authentication" state={{from: location}} replace></Navigate>;
    // if (!user || Object.keys(user).length === 0) {
    //     return <Navigate to="/authentication" state={{ from: location }} replace />;
    //   }
    
    //   return children;
};

export default ProtectedRoute;