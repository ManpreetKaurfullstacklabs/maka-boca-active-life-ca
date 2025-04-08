import "./Login.css";
import {useLocation, useNavigate} from "react-router-dom";
import React, { useState } from "react";
import {toast, ToastContainer} from "react-toastify";
import {initializeCartFromLocalStorage} from "../../redux/CartSlice.js";
import {useDispatch} from "react-redux";

const Otp = () => {
    const { state: { memberLoginId } } = useLocation()
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        memberLoginId,
        pin: "",
    });

    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const [formSubmitted, setFormSubmitted] = useState(false);
    const dispatch = useDispatch();


    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError("");
        try {
            const stream = await fetch("http://localhost:40015/api/familyregistration/login/verify", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(formData),
            }).then((res) => {
                if (res.ok) {
                    return res
                } else {
                    setError(  "Invalid credentials. Please try again.");
                }
            })
            const res = await stream.text()
            console.log(res)
            localStorage.setItem('jwtToken', res);
            localStorage.setItem('memberLoginId',memberLoginId)
            dispatch(initializeCartFromLocalStorage());
            setFormSubmitted(true);
            console.log(res);
            navigate('/registration', { state: { responseData: res,memberLoginId:memberLoginId }});

        } catch (error) {
            console.error("Error:", error);
            toast.error(' Error while adding to cart:', {
                position: 'center',
            });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            {!formSubmitted ? (
                <div className="signup-box">
                    {/*<div className="login-box">*/}
                        <h2>Otp Verification</h2>
                        <form onSubmit={handleSubmit}>
                            <input
                                type="text"
                                className="input-field"
                                name="pin"
                                placeholder="Otp Number"
                                value={formData.pin}
                                onChange={handleChange}
                                required
                            />
                            <h1></h1>
                            <button type="submit" className="login-btn"  disabled={loading}>
                                {loading ? "Verifying..." : "Verify"}
                            </button>
                            <ToastContainer
                                position="bottom-center"
                                autoClose={1500}
                                hideProgressBar
                                closeOnClick
                                pauseOnHover={false}
                                draggable={false}
                            />
                        </form>
                        {error && <p className="error-message">{error}</p>}
                    </div>
                // </div>
            ) : (
                <div>
                    <h3>OTP Verified! Redirecting...</h3>
                </div>
            )}
        </div>
    );
};

export default Otp;
