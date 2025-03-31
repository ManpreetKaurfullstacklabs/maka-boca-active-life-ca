import React from "react";
import "./Login.css"; // Import CSS file

const Login = () => {
    return (
        <div className="login-container">
            <div className="login-box">
                <h2>Login</h2>
                <form>
                    <input type="text" className="input-field" placeholder="Username" required />
                    <input type="password" className="input-field" placeholder="Password" required />
                    <button type="submit" className="login-btn">Login</button>
                </form>
            </div>
        </div>
    );
};

export default Login;
