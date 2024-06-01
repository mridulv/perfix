import React, { useContext } from 'react';
import Loading from '../components/Loading';
import { AuthContext } from '../contexts/AuthProvider';
import Authentication from '../Pages/Authentication/Authentication';

const ProtectedRoute = ({children}) => {
    const {user, userLoading} = useContext(AuthContext);

    if(userLoading){
        return <Loading/>
    }

    if(Object.keys(user).length > 0){
        return children;
    }
    return <Authentication/>;
    
    
};

export default ProtectedRoute;