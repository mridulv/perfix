import React, { useContext } from 'react';
import { AuthContext } from '../contexts/AuthProvider';
import Authentication from '../Pages/Authentication/Authentication';
import Loading from '../components/Common/Loading';

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