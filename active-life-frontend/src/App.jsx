import React from "react";
import {BrowserRouter, Routes, Route, useNavigate} from "react-router-dom";
import Dashboard from "./dashboard/mainpage/Dashboard";
import About from "./dashboard/about/About";
import Signup from "./dashboard/signup/Signup.jsx";
import Login from "./dashboard/login/Login.jsx";
import "./dashboard/mainpage/dropdown.css"
import Otp from "./dashboard/login/Otp.jsx";
import Registration from "./dashboard/registration/Registration.jsx";


function Navigation() {


    const navigate = useNavigate();
    const handleNavigation = (path) => {
        navigate(path);
    };
    return (
        <nav>
            <div className="top-nav">
                <ul>
                    <li onClick={() => handleNavigation("/Home")}>Home</li>
                    <li onClick={() => handleNavigation("/facilities")}>Locations</li>
                    <li onClick={() => handleNavigation("/courses")}>Services</li>
                    <div className="dropdown">
                        <li onClick={() => handleNavigation("/registration")}>Registration</li>
                        <div className="dropdown-content">
                            <li onClick={() => handleNavigation("/login")}>Login</li>
                            <li onClick={() => handleNavigation("/signup")}>SignUp</li>
                        </div>
                    </div>

                    <li onClick={() => handleNavigation("/about")}>About</li>
                    <li onClick={() => handleNavigation("/contacts")}>Contacts</li>

                </ul>
            </div>
        </nav>
    )
}


const App = () => {


    return (
        <BrowserRouter>
            <Navigation/>
            <Routes>
                <Route path="/" element={<Dashboard />} />
                <Route path="/about" element={<About />} />
                <Route path="/signup" element={<Signup/>} />
                <Route path="/login" element={<Login/>} />
                <Route path="/otp" element={<Otp/>} />
                <Route path={"/registration"} element={<Registration/>}/>
            </Routes>
        </BrowserRouter>
    );
};

export default App;
