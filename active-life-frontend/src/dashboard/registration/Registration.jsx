import React from "react";
import "./Registration.css";
import { useLocation } from "react-router";

const Registration = () => {

    const { state } = useLocation()




    return (

        <div className="login-container">
            <div className="login-box">
                <h2>Registration page</h2>
                <form>
                    <h2>hurray !!!!!!</h2>
                    {/*<input type="text" className="input-field" placeholder="Username" required />*/}
                    {/*<input type="password" className="input-field" placeholder="Password" required />*/}
                    {/*<h1></h1>*/}
                    {/*<button type="submit" className="login-btn">Login</button>*/}
                </form>
            </div>
        </div>
    );
};

export default Registration
