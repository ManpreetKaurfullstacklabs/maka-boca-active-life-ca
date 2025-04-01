import React from "react";
import "./Login.css";
import {useNavigate} from "react-router-dom";

const Otp = () => {
    const navigate = useNavigate();
    const handleNavigation = (path) => {
        navigate(path);
    };
    return (
        <div className="login-container">
            <div className="login-box">
                <h2>Otp Verification</h2>
                <form>
                    <input type="text" className="input-field" placeholder="Otp Number" required />
                    <h1></h1>
                    <button type="submit" className="login-btn" onClick={()=> handleNavigation("/registration")}>Verify</button>
                </form>
            </div>
        </div>
    );
};

export default Otp;