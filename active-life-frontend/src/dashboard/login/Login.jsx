import React, { useState } from "react";
import "./Login.css";
import { useNavigate } from "react-router-dom";
import {toast, ToastContainer} from "react-toastify";
import {Link} from "react-router";

const Login = () => {
    const navigate = useNavigate();

    const blankedState = {
        memberLoginId: "",
        pin: "",
    };
    const [formData, setFormData] = useState(blankedState);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

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
            const res = await fetch("http://localhost:40015/api/familyregistration/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData),
            });
            if (res.ok) {
                const responseData = await res.json();
                console.log("API Response:", responseData);
                toast.success('Credential Verified !', {
                    position: 'center',
                });
                navigate('/otp', { state: { responseData, memberLoginId:  formData.memberLoginId} });
            } else {
                if (res.status === 401) {
                    setError("Activation required. check your phone");
                    }
                if(res.status===400){
                    setError("Member does not exist Signup Please")
                }
            }
        } catch (error) {
            console.error("Error:", error);
            toast.error('Invalid credentials. Please try again.');

        } finally {
            setLoading(false);
        }
    };

    return (

        <div className="login-container">
            {/*<ToastContainer />*/}
            <div className="login-box">
                <h2>Login</h2>
                {error && <p className="error-message">{error}</p>} {/* Display error message */}
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        name="memberLoginId"
                        className="input-field"
                        placeholder="Username"
                        onChange={handleChange}
                        value={formData.memberLoginId}
                        required
                    />
                    <input
                        type="password"
                        name="pin"
                        className="input-field"
                        placeholder="Password"
                        onChange={handleChange}
                        value={formData.pin}
                        required
                    />
                    <button type="submit" className="login-btn" disabled={loading}>
                        {loading ? "Logging in..." : "Login"}

                        <ToastContainer
                            position="bottom-center"
                            autoClose={1500}
                            hideProgressBar
                            closeOnClick
                            pauseOnHover={false}
                            draggable={false}
                        />
                    </button>
                </form>
                <p>Don’t have an account? <Link to="/signup">Signup</Link></p>
            </div>
        </div>
    );
};

export default Login;
