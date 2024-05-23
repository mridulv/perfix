import React, { useContext, useEffect } from 'react';
import { AuthContext } from '../../contexts/AuthProvider';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';

const Authentication = () => {
    const {user, setUser} = useContext(AuthContext);
    const navigate = useNavigate();
    const location = useLocation();
    // useEffect(() => {
    //     if(user){
    //         navigate("/")
    //     }
    // }, [user, navigate])
    // useEffect(() => {
    //     if (user && location.pathname !== '/') {
    //       navigate('/');
    //     }
    //   }, [user, navigate, location.pathname]);



    const handleUser = () => {
        window.location.href = 'http://localhost:9001/login';
    }
   
    if(user && Object.keys(user).length > 0){
        navigate("/")
    }

    return (
        <div className='min-h-screen flex flex-col items-center justify-center'>
            <h4>Please Login</h4>
            <button onClick={handleUser} className='btn btn-secondary'>login</button>
        </div>
    );
};

export default Authentication;