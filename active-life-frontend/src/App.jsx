import React, {useEffect, useState} from "react";
import {BrowserRouter, Routes, Route, useNavigate, Outlet} from "react-router-dom";
import Dashboard from "./dashboard/mainpage/Dashboard";
import About from "./dashboard/about/About";
import Signup from "./dashboard/signup/Signup.jsx";
import Login from "./dashboard/login/Login.jsx";
import "./dashboard/mainpage/dropdown.css"
import Otp from "./dashboard/login/Otp.jsx";
import Registration from "./dashboard/registration/Registration.jsx";
import CourseDescription from "./dashboard/registration/CourseDescription.jsx";
import Cart from "./dashboard/Cart.jsx";
import {useSelector} from "react-redux";

function CommonHeader () {
    const [member, setMember] = useState([])
    const  authToken = localStorage.getItem("jwtToken")
    const loginId = localStorage.getItem("memberLoginId")
    const navigate = useNavigate();
    const cartCount = useSelector(state => state?.cart?.items)?.length

    useEffect(() => {

        const wrapperFn = async () => {
            const memberInfo = await fetch("http://localhost:40015/api/familyregistration/" + loginId, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${authToken}`
                    }
                }
            );
            if (memberInfo.ok) {
                const memberData = await memberInfo.json();
                setMember(memberData)
                console.log(memberData)
            }
        }
        wrapperFn()
    }, [])

    if (!member) {
       return  <Outlet />
    }
    const handleLogout = () => {
        localStorage.removeItem("jwtToken");
        localStorage.removeItem("memberLoginId");
    };


    return (
        <div>
            <h1>Offered Courses</h1>
            <div className="item-container">
                <div className="top-nav-bar">
                    <div className="member-name">
                        Welcome {member && member.memberName ? member.memberName : "Member Name"}
                    </div>
                    <div className={"logout"}>
                        <i className="fa badge fa-lg" value={cartCount} onClick={() => navigate("/cart")} style={{ cursor: "pointer" }}>
                            &#xf07a;
                        </i>
                        <button onClick={() => {handleLogout()}}>Logout</button>
                    </div>

                </div>
                <Outlet />
            </div>
        </div>
    )
}

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

                <Route element={<CommonHeader/>}>
                    <Route path={"/registration"} element={<Registration/>}/>
                    <Route path ="/CourseDescription/:id" element={<CourseDescription/>}/>
                    <Route path ={"/Cart"} element={<Cart/>}/>
                </Route>

            </Routes>
        </BrowserRouter>
    );
};

export default App;
