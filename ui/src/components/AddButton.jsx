import React from 'react';
import { Link } from 'react-router-dom';

const AddButton = ({value, link, type=null}) => {
    return (
        
          <Link
            className="btn bg-[#E5227A] btn-sm border border-[#E5227A] rounded text-white hover:bg-[#6b3b51d2]"
            to={link}
            type={type}
          >
            {value}
          </Link>
        
    );
};

export default AddButton;