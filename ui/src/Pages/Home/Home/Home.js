import React from 'react';
import Banner from '../Banner/Banner';
import Features from '../Features/Features';
import Design from '../../../components/Design';

const Home = () => {
    return (
        <div className='bg-[#000] min-h-screen flex flex-col justify-center my-auto'>
            <Design/>
            <Banner/>
        </div>
    );
};

export default Home;